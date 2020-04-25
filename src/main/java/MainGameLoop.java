import entity.Player;
import entity.env.Camera;
import entity.Entity;
import entity.env.Light;
import model.data.ModelData;
import model.TexturedModel;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import renderEngine.*;
import model.RawModel;
import texture.ModelTexture;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainGameLoop {

    public static void main(String[] args) throws IOException, URISyntaxException {
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        ModelTexture whiteTexture = new ModelTexture(loader.loadTexture("white"));
        ModelTexture purpleTexture = new ModelTexture(loader.loadTexture("purple"));

        ModelData quadModelData = ObjectLoader.loadObjectModel("quad");
        RawModel rawQuadModel = loader.loadToVAO(quadModelData.getVertices(), quadModelData.getIndices(), quadModelData.getNormals(), quadModelData.getTextureCoords());
        TexturedModel quadModel = new TexturedModel(rawQuadModel, purpleTexture);
        quadModel.getTexture().isTransparent(true);

//        ModelData roomModelData = ObjectLoader.loadObjectModel("room");
//        RawModel rawRoomModel = loader.loadToVAO(roomModelData.getVertices(), roomModelData.getIndices(), roomModelData.getNormals(), roomModelData.getTextureCoords());
//        TexturedModel roomModel = new TexturedModel(rawRoomModel, whiteTexture);

        ModelData playerModelData = ObjectLoader.loadObjectModel("player");
        RawModel rawPlayerModel = loader.loadToVAO(playerModelData.getVertices(), playerModelData.getIndices(), playerModelData.getNormals(), playerModelData.getTextureCoords());

        TexturedModel playerModel = new TexturedModel(rawPlayerModel, purpleTexture);

        ModelData sphereModelData = ObjectLoader.loadObjectModel("sphere");
        RawModel rawSphereModel = loader.loadToVAO(sphereModelData.getVertices(), sphereModelData.getIndices(), sphereModelData.getNormals(), sphereModelData.getTextureCoords());
        TexturedModel sphereModel = new TexturedModel(rawSphereModel, whiteTexture);
        sphereModel.getTexture().isTransparent(true);


//        Entity room = new Entity(roomModel, new Vector3D(0, 0, 0), 0,0,0,new Vector3D(10));
        Entity quad = new Entity(quadModel, new Vector3f(0,0,-20), 0, 90, 0, new Vector3f(20));
        Player player = new Player(playerModel, new Vector3f(0,0,0), 0,180,0,new Vector3f(3));
        Entity sphere = new Entity(sphereModel, new Vector3f(0,0,0),0,0,0,new Vector3f(6,10,2));
        List<Entity> entities = new ArrayList<>(List.of(quad, player));


        Light light = new Light(new Vector3f(30, 30, 100), new Vector3f(1, 1, 1));
        ParentRenderer renderer = new ParentRenderer();
        Camera camera = new Camera(player);
        while (!GLFW.glfwWindowShouldClose(DisplayManager.getWindow())) {
            List<Entity> temp = new ArrayList<>(List.copyOf(entities));
            temp.remove(sphere);
            player.move(temp);
            sphere.setPosition(new Vector3f(player.getPosition().x, player.getPosition().y + 9, player.getPosition().z - 1));
            camera.move();
            renderer.processEntities(entities);
            renderer.renderObjects(light, camera);
            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
