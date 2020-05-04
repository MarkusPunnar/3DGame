package renderEngine;

import interraction.InteractionHandler;
import object.Entity;
import object.ObjectLoader;
import object.Player;
import model.RawModel;
import model.TexturedModel;
import model.data.ModelData;
import object.scene.Door;
import org.joml.Vector3f;
import texture.ModelTexture;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SceneGenerator {

    private Loader loader;
    private InteractionHandler handler;

    public SceneGenerator(Loader loader) {
        this.loader = loader;
    }

    public List<Entity> generateRoom() throws IOException, URISyntaxException {
        List<Entity> roomEntities = new ArrayList<>();
        //Generate room background
        TexturedModel roomModel = getTexturedModel("room");
        roomModel.getTexture().isTransparent(true);
        Entity room = new Entity(roomModel, new Vector3f(0,0,0), 0, 0, 0, new Vector3f(1));
        roomEntities.add(room);
        //Generate stools
        TexturedModel stoolModel = getTexturedModel("stool");
        for (int i = 0; i < 3; i++) {
            roomEntities.add(new Entity(stoolModel, new Vector3f(3 + 12 * i, 0, 25), 0, 0, 0, new Vector3f(1)));
        }
        for (int i = 0; i < 4; i++) {
            roomEntities.add(new Entity(stoolModel, new Vector3f(-13, 0,8 - 15 * i), 0, 0, 0, new Vector3f(1)));
        }
        //Generate barrels
        TexturedModel barrelModel = getTexturedModel("barrel");
        roomEntities.add(new Entity(barrelModel, new Vector3f(3,0,10),0,0,0, new Vector3f(5)));
        roomEntities.add(new Entity(barrelModel, new Vector3f(28,0,-62),0,0,0, new Vector3f(5)));
        roomEntities.add(new Entity(barrelModel, new Vector3f(30,0,-33),0,0,0, new Vector3f(5)));
        //Generate tables and chairs
        TexturedModel tableModel = getTexturedModel("table");
        roomEntities.addAll(generateTableWithChairs(tableModel, stoolModel, new Vector3f(-27, 1.5f, 100)));
        roomEntities.addAll(generateTableWithChairs(tableModel, stoolModel, new Vector3f(15, 1.5f, 103)));
        //Generate door
        Door door = new Door(getTexturedModel("door"), new Vector3f(-44, 0, -26), 0, 0, 0, new Vector3f(1));
        handler.addObject(door);
        roomEntities.add(door);
        return roomEntities;
    }

    private List<Entity> generateTableWithChairs(TexturedModel tableModel, TexturedModel stoolModel, Vector3f tablePosition) {
        List<Entity> tables = new ArrayList<>();
        tables.add(new Entity(tableModel, tablePosition, 0, 0, 0, new Vector3f(0.75f)));
        tables.add(new Entity(stoolModel, new Vector3f(tablePosition.x + 12, 1, tablePosition.z - 13), 0, 0, 0, new Vector3f(1, 0.8f, 1)));
        tables.add(new Entity(stoolModel, new Vector3f(tablePosition.x - 10, 1, tablePosition.z - 13), 0, 0, 0, new Vector3f(1, 0.8f, 1)));
        tables.add(new Entity(stoolModel, new Vector3f(tablePosition.x - 10, 1, tablePosition.z + 9), 0, 0, 0, new Vector3f(1, 0.8f, 1)));
        tables.add(new Entity(stoolModel, new Vector3f(tablePosition.x + 12, 1, tablePosition.z + 9), 0, 0, 0, new Vector3f(1, 0.8f, 1)));
        return tables;
    }

    public Player generatePlayer(Loader loader) throws IOException, URISyntaxException {
        ModelTexture purpleTexture = new ModelTexture(loader.loadTexture("purple"));
        TexturedModel playerModel = getTexturedModel("player", purpleTexture);
        return new Player(playerModel, new Vector3f(20,0,50),0,180,0, new Vector3f(3));
    }

    private TexturedModel getTexturedModel(String objName, ModelTexture texture) throws IOException, URISyntaxException {
        ModelData modelData = ObjectLoader.loadObjectModel(objName);
        RawModel rawModel = loader.loadToVAO(modelData.getVertices(), modelData.getIndices(), modelData.getNormals(), modelData.getTextureCoords());
        return new TexturedModel(rawModel, texture);
    }

    private TexturedModel getTexturedModel(String fileName) throws URISyntaxException, IOException {
        ModelData modelData = ObjectLoader.loadObjectModel(fileName);
        RawModel rawModel = loader.loadToVAO(modelData.getVertices(), modelData.getIndices(), modelData.getNormals(), modelData.getTextureCoords());
        ModelTexture texture =  new ModelTexture(loader.loadTexture(fileName));
        return new TexturedModel(rawModel, texture);
    }

    public void setInteractionHandler(InteractionHandler handler) {
        this.handler = handler;
    }
}
