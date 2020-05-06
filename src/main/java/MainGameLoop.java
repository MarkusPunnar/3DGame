import interraction.InteractionHandler;
import loader.Loader;
import object.Player;
import object.env.Camera;
import object.Entity;
import object.env.Light;
import interraction.MousePicker;
import object.scene.SceneGenerator;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import renderEngine.*;
import texture.GuiTexture;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainGameLoop {

    public static void main(String[] args) throws IOException, URISyntaxException {
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        ParentRenderer renderer = new ParentRenderer(loader);
        SceneGenerator sceneGenerator = new SceneGenerator(loader);
        Player player = sceneGenerator.generatePlayer(loader);
        InteractionHandler interactionHandler = new InteractionHandler(player);
        sceneGenerator.setInteractionHandler(interactionHandler);
        List<Entity> roomEntities = sceneGenerator.generateTavern();
        List<GuiTexture> guis = new ArrayList<>();
        guis.add(new GuiTexture(loader.loadTexture("purple"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f)));
        Light light = new Light(new Vector3f(30, 50, 100), new Vector3f(1, 1, 1));
        Camera camera = new Camera(player);
        MousePicker mousePicker = new MousePicker(renderer.getProjectionMatrix(), camera);
        while (!GLFW.glfwWindowShouldClose(DisplayManager.getWindow())) {
            player.move(roomEntities);
            camera.move();
            mousePicker.update();
            interactionHandler.checkInteractions();
            renderer.processEntities(roomEntities, player);
            renderer.processGuis(guis);
            renderer.renderObjects(light, camera);
            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
