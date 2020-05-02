import interraction.InteractionHandler;
import object.Player;
import object.env.Camera;
import object.Entity;
import object.env.Light;
import interraction.MousePicker;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import renderEngine.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class MainGameLoop {

    public static void main(String[] args) throws IOException, URISyntaxException {
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        ParentRenderer renderer = new ParentRenderer();
        SceneGenerator sceneGenerator = new SceneGenerator(loader);
        Player player = sceneGenerator.generatePlayer(loader);
        InteractionHandler interactionHandler = new InteractionHandler(player);
        sceneGenerator.setInteractionHandler(interactionHandler);
        List<Entity> roomEntities = sceneGenerator.generateRoom();
        Light light = new Light(new Vector3f(30, 30, 100), new Vector3f(1, 1, 1));
        Camera camera = new Camera(player);
        MousePicker mousePicker = new MousePicker(renderer.getProjectionMatrix(), camera);
        while (!GLFW.glfwWindowShouldClose(DisplayManager.getWindow())) {
            player.move(roomEntities);
            camera.move();
            mousePicker.update();
            interactionHandler.checkInteractions();
//            System.out.println(mousePicker.getCurrentRay());
            renderer.processEntities(roomEntities, player);
            renderer.renderObjects(light, camera);
            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
