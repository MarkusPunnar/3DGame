package renderEngine;

import entity.Entity;
import entity.Player;
import model.RawModel;
import model.TexturedModel;
import model.data.ModelData;
import org.joml.Vector3f;
import texture.ModelTexture;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SceneGenerator {

    private Loader loader;

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
        roomEntities.add(new Entity(stoolModel, new Vector3f(-15, 0, 87), 0, 0, 0, new Vector3f(1, 0.8f, 1)));
        roomEntities.add(new Entity(stoolModel, new Vector3f(-37, 0, 87), 0, 0, 0, new Vector3f(1, 0.8f, 1)));
        roomEntities.add(new Entity(stoolModel, new Vector3f(-15, 0, 109), 0, 0, 0, new Vector3f(1, 0.8f, 1)));
        roomEntities.add(new Entity(stoolModel, new Vector3f(-37, 0, 109), 0, 0, 0, new Vector3f(1, 0.8f, 1)));
        //Generate barrels
        TexturedModel barrelModel = getTexturedModel("barrel");
        roomEntities.add(new Entity(barrelModel, new Vector3f(3,0,10),0,0,0, new Vector3f(5)));
        roomEntities.add(new Entity(barrelModel, new Vector3f(28,0,-62),0,0,0, new Vector3f(5)));
        roomEntities.add(new Entity(barrelModel, new Vector3f(30,0,-33),0,0,0, new Vector3f(5)));
        //Generate table
        roomEntities.add(new Entity(getTexturedModel("table"), new Vector3f(-27, 0, 100), 0, 0, 0, new Vector3f(0.75f)));
        //Generate door
        roomEntities.add(new Entity(getTexturedModel("door"), new Vector3f(-44, 0, -26), 0, 0, 0, new Vector3f(1)));
        return roomEntities;
    }

    public Player generatePlayer(Loader loader) throws IOException, URISyntaxException {
        ModelTexture purpleTexture = new ModelTexture(loader.loadTexture("purple"));
        TexturedModel playerModel = getTexturedModel("player", purpleTexture);
        return new Player(playerModel, new Vector3f(-20,0,50),0,180,0, new Vector3f(3));
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
}
