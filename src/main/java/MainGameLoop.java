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
        ParentRenderer renderer = new ParentRenderer();
        SceneGenerator sceneGenerator = new SceneGenerator();
        List<Entity> roomEntities = sceneGenerator.generateRoom(loader);
        Player player = sceneGenerator.generatePlayer(loader);
        Light light = new Light(new Vector3f(30, 30, 100), new Vector3f(1, 1, 1));
        Camera camera = new Camera(player);
        while (!GLFW.glfwWindowShouldClose(DisplayManager.getWindow())) {
            player.move(roomEntities);
            camera.move();
            renderer.processEntities(roomEntities, player);
            renderer.renderObjects(light, camera);
            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
