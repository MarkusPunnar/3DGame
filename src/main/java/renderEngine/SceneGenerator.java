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

    public List<Entity> generateRoom(Loader loader) throws IOException, URISyntaxException {
        List<Entity> roomEntities = new ArrayList<>();

        ModelTexture woodTexture = new ModelTexture(loader.loadTexture("wood"));
        ModelTexture whiteTexture = new ModelTexture(loader.loadTexture("white"));
        ModelTexture barrelTexture = new ModelTexture(loader.loadTexture("barrel"));

        TexturedModel roomModel = getTexturedModel(loader, "room", whiteTexture);
        roomModel.getTexture().isTransparent(true);
        Entity room = new Entity(roomModel, new Vector3f(0,0,0), 0, 0, 0, new Vector3f(1));
        roomEntities.add(room);

        TexturedModel stoolModel = getTexturedModel(loader, "stool", woodTexture);

        for (int i = 0; i < 3; i++) {
            roomEntities.add(new Entity(stoolModel, new Vector3f(3 + 12 * i, 0, 25), 0, 0, 0, new Vector3f(1)));
        }

        for (int i = 0; i < 4; i++) {
            roomEntities.add(new Entity(stoolModel, new Vector3f(-13, 0,8 - 15 * i), 0, 0, 0, new Vector3f(1)));
        }

        TexturedModel barrelModel = getTexturedModel(loader, "barrel", barrelTexture);
        roomEntities.add(new Entity(barrelModel, new Vector3f(3,0,10),0,0,0, new Vector3f(5)));
        roomEntities.add(new Entity(barrelModel, new Vector3f(28,0,-62),0,0,0, new Vector3f(5)));
        roomEntities.add(new Entity(barrelModel, new Vector3f(30,0,-33),0,0,0, new Vector3f(5)));

        return roomEntities;
    }

    public Player generatePlayer(Loader loader) throws IOException, URISyntaxException {
        ModelTexture purpleTexture = new ModelTexture(loader.loadTexture("purple"));
        TexturedModel playerModel = getTexturedModel(loader, "player", purpleTexture);
        return new Player(playerModel, new Vector3f(0,0,50),0,180,0, new Vector3f(3));
    }

    private TexturedModel getTexturedModel(Loader loader, String objName, ModelTexture texture) throws IOException, URISyntaxException {
        ModelData modelData = ObjectLoader.loadObjectModel(objName);
        RawModel rawModel = loader.loadToVAO(modelData.getVertices(), modelData.getIndices(), modelData.getNormals(), modelData.getTextureCoords());
        return new TexturedModel(rawModel, texture);
    }
}
