package game;

import engine.DisplayManager;
import engine.loader.Loader;
import engine.render.ParentRenderer;
import object.RenderObject;
import game.state.GameState;
import game.state.State;
import interraction.MousePicker;
import interraction.handle.*;
import object.Entity;
import object.Player;
import object.env.Camera;
import object.env.Light;
import object.scene.generation.TavernGenerator;
import object.scene.generation.TerrainGenerator;
import object.terrain.Terrain;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import util.octree.BoundingBox;
import util.octree.OctTree;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MainGameLoop {

    public static void main(String[] args) throws IOException, URISyntaxException {
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        ParentRenderer renderer = new ParentRenderer(loader);
        TavernGenerator tavernGenerator = new TavernGenerator(loader);
        TerrainGenerator terrainGenerator = new TerrainGenerator(loader);
        Player player = tavernGenerator.generatePlayer(loader);
        List<Light> lights = new ArrayList<>();
        Light sun = new Light(new Vector3f(3000, 5000, 10000), new Vector3f(0.3f));
        lights.add(sun);
        List<Entity> roomEntities = tavernGenerator.generate(lights);
        List<Terrain> terrains = terrainGenerator.generate(lights);
        OctTree octTree =  new OctTree(new BoundingBox(new Vector3f(-400, -1, -400), new Vector3f(200, 100, 200)));

        Camera camera = new Camera(player);
        MousePicker mousePicker = new MousePicker(renderer.getProjectionMatrix(), camera);
        camera.setMousePicker(mousePicker);
        initCallbacks(player);
        List<Handler> handlers = initHandlers(player, renderer, mousePicker);
        List<RenderObject> renderObjects = new ArrayList<>(roomEntities);
        renderObjects.addAll(terrains);
        octTree.initTree(renderObjects);
        GameState.getInstance().setCurrentTree(octTree);

        while (!GLFW.glfwWindowShouldClose(DisplayManager.getWindow())) {
            camera.checkState();
            if (GameState.getInstance().getCurrentState() == State.IN_GAME) {
                player.move(renderObjects);
                camera.move();
            }
            mousePicker.update();
            renderer.processTerrains(terrains);
            renderer.processEntities(roomEntities, player);
            for (Handler handler : handlers) {
                handler.handle();
            }
            renderer.renderObjects(lights, camera);
            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

    private static List<Handler> initHandlers(Player player, ParentRenderer renderer, MousePicker mousePicker) throws IOException, URISyntaxException {
        List<Handler> handlers = new ArrayList<>();
        handlers.add(new InteractionHandler(player));
        handlers.add(new RenderRequestHandler(renderer, player, "gamefont"));
        handlers.add(new InventoryHandler(player, mousePicker));
        handlers.add(new LootingHandler(player, mousePicker));
        return handlers;
    }

    private static void initCallbacks(Player player) {
        GameState state =  GameState.getInstance();
        GLFW.glfwSetKeyCallback(DisplayManager.getWindow(), ((window, key, scancode, action, mods) -> {
           if (key == GLFW.GLFW_KEY_I && action == GLFW.GLFW_PRESS && List.of(State.IN_GAME, State.IN_INVENTORY).contains(state.getCurrentState())) {
               player.interactWithInventory();
           }
           else if (key == GLFW.GLFW_KEY_F && action == GLFW.GLFW_PRESS && List.of(State.IN_GAME, State.IN_CHEST).contains(state.getCurrentState())) {
               player.interactWithObject();
           }
           else if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE && state.getCurrentState().equals(State.IN_GAME)) {
               GLFW.glfwSetWindowShouldClose(window, true);
           }
        }));
    }
}
