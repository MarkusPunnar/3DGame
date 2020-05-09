package game;

import engine.DisplayManager;
import engine.loader.Loader;
import engine.render.ParentRenderer;
import game.state.GameState;
import interraction.handle.*;
import game.state.State;
import interraction.MousePicker;
import object.Entity;
import object.Player;
import object.env.Camera;
import object.env.Light;
import object.scene.TavernGenerator;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainGameLoop {

    public static void main(String[] args) throws IOException, URISyntaxException {
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        ParentRenderer renderer = new ParentRenderer(loader);
        GameState gameState = new GameState();
        TavernGenerator tavernGenerator = new TavernGenerator(loader, gameState);
        Player player = tavernGenerator.generatePlayer(loader);
        List<Entity> roomEntities = tavernGenerator.generate();
        Light light = new Light(new Vector3f(30, 50, 100), new Vector3f(1, 1, 1));
        Camera camera = new Camera(player);
        MousePicker mousePicker = new MousePicker(renderer.getProjectionMatrix(), camera);
        gameState = initCallbacks(gameState, player);
        List<Handler> handlers = initHandlers(player, renderer, mousePicker);
        while (!GLFW.glfwWindowShouldClose(DisplayManager.getWindow())) {
            if (gameState.getCurrentState() == State.IN_GAME) {
                player.move(roomEntities);
                camera.move();
            }
            mousePicker.update();
            renderer.processEntities(roomEntities, player);
            for (Handler handler : handlers) {
                gameState = handler.handle(gameState);
            }
            renderer.renderObjects(light, camera);
            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

    private static List<Handler> initHandlers(Player player, ParentRenderer renderer, MousePicker mousePicker) {
        List<Handler> handlers = new ArrayList<>();
        handlers.add(new InteractionHandler(player));
        handlers.add(new RenderRequestHandler(renderer, player));
        handlers.add(new InventoryHandler(player, mousePicker));
        handlers.add(new LootingHandler(player, mousePicker));
        return handlers;
    }

    private static GameState initCallbacks(GameState state, Player player) {
        AtomicReference<GameState> newState = new AtomicReference<>();
        newState.set(state);
        GLFW.glfwSetKeyCallback(DisplayManager.getWindow(), ((window, key, scancode, action, mods) -> {
           if (key == GLFW.GLFW_KEY_I && action == GLFW.GLFW_PRESS && List.of(State.IN_GAME, State.IN_INVENTORY).contains(state.getCurrentState())) {
               player.interactWithInventory(state);
           }
           else if (key == GLFW.GLFW_KEY_F && action == GLFW.GLFW_PRESS && List.of(State.IN_GAME, State.IN_CHEST).contains(state.getCurrentState())) {
               newState.set(player.interactWithObject(state));
           }
        }));
        return newState.get();
    }
}
