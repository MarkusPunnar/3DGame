package game.state;

import com.google.common.flogger.FluentLogger;
import engine.DisplayManager;
import engine.loader.VAOLoader;
import engine.render.ParentRenderer;
import engine.texture.GuiTexture;
import interraction.MousePicker;
import interraction.handle.*;
import object.Entity;
import object.Player;
import object.RenderObject;
import object.env.Camera;
import object.env.Light;
import object.generation.MainMenuGenerator;
import object.generation.TavernGenerator;
import object.generation.TerrainGenerator;
import object.terrain.Terrain;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import util.octree.BoundingBox;
import util.octree.OctTree;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class Game {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static final Game INSTANCE = new Game();

    private State currentState;
    private OctTree currentTree;

    private List<Handler> activeHandlers;
    private List<Light> activeLights;
    private List<RenderObject> activeObjects;

    private ParentRenderer renderer;
    private VAOLoader loader;
    private Camera playerCamera;
    private MousePicker mousePicker;
    private Player player;

    private Game() {
        this.activeHandlers = new ArrayList<>();
        this.activeLights = new ArrayList<>();
        this.activeObjects = new ArrayList<>();
    }

    public static Game getInstance() {
        return INSTANCE;
    }

    public void init() throws IOException, URISyntaxException {
        currentState = State.IN_MENU;
        loader = new VAOLoader();
        MainMenuGenerator menuGenerator = new MainMenuGenerator();
        renderer = new ParentRenderer();
        List<GuiTexture> menuGuis = menuGenerator.generate();
        renderer.processGuis(menuGuis);
    }

    public void loadGame() throws IOException, URISyntaxException {
        currentState = State.IN_GAME;
        activeLights.add(new Light(new Vector3f(3000, 5000, 3000), new Vector3f(1), false, null));
        loadRenderObjects();
        initCallbacks();
        initHandlers();
        playerCamera = new Camera();
        renderer.load(playerCamera, activeLights);
        mousePicker = new MousePicker();
        OctTree octTree = new OctTree(new BoundingBox(new Vector3f(-400, -1, -400), new Vector3f(200, 100, 200)));
        octTree.initTree(activeObjects);
        currentTree = octTree;
        logger.atInfo().log("Octree initialized with %d objects", activeObjects.size());
    }

    private void loadRenderObjects() throws IOException, URISyntaxException {
        TavernGenerator tavernGenerator = new TavernGenerator();
        TerrainGenerator terrainGenerator = new TerrainGenerator();
        player = tavernGenerator.generatePlayer();
        List<Entity> roomEntities = tavernGenerator.generate();
        renderer.processEntities(roomEntities, player);
        List<Terrain> terrains = terrainGenerator.generate();
        renderer.processTerrains(terrains);
        activeObjects.addAll(roomEntities);
        activeObjects.addAll(terrains);
    }

    private void initHandlers() throws IOException, URISyntaxException {
        List<Handler> handlers = new ArrayList<>();
        handlers.add(new InteractionHandler());
        handlers.add(new RenderRequestHandler("gamefont"));
        handlers.add(new InventoryHandler());
        handlers.add(new LootingHandler());
        logger.atInfo().log("Initialized handlers");
        activeHandlers = handlers;
    }

    private void initCallbacks() {
        GLFW.glfwSetKeyCallback(DisplayManager.getWindow(), ((window, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_I && action == GLFW.GLFW_PRESS && List.of(State.IN_GAME, State.IN_INVENTORY).contains(currentState)) {
                player.interactWithInventory();
            } else if (key == GLFW.GLFW_KEY_F && action == GLFW.GLFW_PRESS && List.of(State.IN_GAME, State.IN_CHEST).contains(currentState)) {
                player.interactWithObject();
            } else if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE && currentState.equals(State.IN_GAME)) {
                GLFW.glfwSetWindowShouldClose(window, true);
            }
        }));
        logger.atInfo().log("Initialized callbacks");
    }

    public void updateGame() throws IOException {
        logger.atInfo().atMostEvery(5, TimeUnit.SECONDS).log("Current FPS: %d", ((int) (1 / DisplayManager.getFrameTime())));
        playerCamera.checkState();
        if (Game.getInstance().getCurrentState() == State.IN_GAME) {
            player.move(activeObjects);
            playerCamera.move(activeObjects);
        }
        mousePicker.update();
        for (Handler handler : activeHandlers) {
            handler.handle();
        }
        renderer.updateDepthMaps(activeLights, player, playerCamera);
        renderer.renderObjects(activeLights, playerCamera);
    }

    public void cleanUp() {
        renderer.cleanUp();
        loader.cleanUp();
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        logger.atInfo().log("Set current game state to %s", currentState);
        this.currentState = currentState;
    }

    public OctTree getCurrentTree() {
        return currentTree;
    }

    public MousePicker getMousePicker() {
        return mousePicker;
    }

    public List<Light> getActiveLights() {
        return activeLights;
    }

    public ParentRenderer getRenderer() {
        return renderer;
    }

    public Camera getPlayerCamera() {
        return playerCamera;
    }

    public Player getPlayer() {
        return player;
    }

    public VAOLoader getLoader() {
        return loader;
    }
}
