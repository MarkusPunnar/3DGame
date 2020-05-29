package game.state;

import com.google.common.flogger.FluentLogger;
import engine.DisplayManager;
import engine.loader.VAOLoader;
import engine.render.ParentRenderer;
import game.ui.menu.Button;
import game.ui.menu.Menu;
import game.ui.UIComponent;
import game.ui.menu.MenuCache;
import game.ui.menu.MenuType;
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

import static org.lwjgl.glfw.GLFW.*;

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
    private MenuType currentMenu;
    private MenuCache menuCache;

    private Game() {
        this.activeHandlers = new ArrayList<>();
        this.activeLights = new ArrayList<>();
        this.activeObjects = new ArrayList<>();
    }

    public static Game getInstance() {
        return INSTANCE;
    }

    public void init() throws IOException, URISyntaxException {
        currentState = State.IN_MAIN_MENU;
        loader = new VAOLoader();
        menuCache = new MenuCache();
        MainMenuGenerator menuGenerator = new MainMenuGenerator();
        renderer = new ParentRenderer();
        currentMenu = MenuType.MAIN_MENU;
        menuCache.addToCache(currentMenu, new Menu());
        List<UIComponent> menuGuis = menuGenerator.generate();
        activeHandlers.add(new MainMenuHandler());
        renderer.processGuis(menuGuis);
        mousePicker = new MousePicker();
        initMenuCallbacks();
    }

    public void loadGame() throws IOException, URISyntaxException {
        activeHandlers.clear();
        renderer.getGuis().clear();
        currentState = State.IN_GAME;
        activeLights.add(new Light(new Vector3f(3000, 5000, 3000), new Vector3f(1), false, null));
        loadRenderObjects();
        initGameCallbacks();
        initGameHandlers();
        playerCamera = new Camera();
        renderer.load(playerCamera, activeLights);
        mousePicker.init();
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

    private void initGameHandlers() throws IOException, URISyntaxException {
        List<Handler> handlers = new ArrayList<>();
        handlers.add(new InteractionHandler());
        handlers.add(new RenderRequestHandler("gamefont"));
        handlers.add(new InventoryHandler());
        handlers.add(new LootingHandler());
        logger.atInfo().log("Initialized game handlers");
        activeHandlers = handlers;
    }

    private void initMenuCallbacks() {
        GLFW.glfwSetMouseButtonCallback(DisplayManager.getWindow(), ((window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                Button activeButton = menuCache.getFromCache(currentMenu).getActiveButton(mousePicker.calculateDeviceCoords());
                if (activeButton != null) {
                    try {
                        if (activeButton.getClickCallback() == null) {
                            logger.atWarning().log("Button callback function is undefined");
                            return;
                        }
                        activeButton.onClick();
                    } catch (Exception e) {
                        logger.atSevere().log("Error occurred during button callback");
                        throw new RuntimeException("Error while button callback");
                    }
                }
            }
        }));
    }

    private void initGameCallbacks() {
        GLFW.glfwSetKeyCallback(DisplayManager.getWindow(), ((window, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_I && action == GLFW.GLFW_PRESS && List.of(State.IN_GAME, State.IN_INVENTORY).contains(currentState)) {
                player.interactWithInventory();
            } else if (key == GLFW.GLFW_KEY_F && action == GLFW.GLFW_PRESS && List.of(State.IN_GAME, State.IN_CHEST).contains(currentState)) {
                player.interactWithObject();
            } else if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE && currentState.equals(State.IN_GAME)) {
                GLFW.glfwSetWindowShouldClose(window, true);
            }
        }));
        logger.atInfo().log("Initialized game callbacks");
    }

    public void update() throws IOException {
        logger.atInfo().atMostEvery(5, TimeUnit.SECONDS).log("Current FPS: %d", ((int) (1 / DisplayManager.getFrameTime())));
        if (currentState != State.IN_MAIN_MENU) {
            updateGame();
        }
        for (Handler handler : activeHandlers) {
            handler.handle();
        }
        renderer.renderObjects(activeLights);
    }



    private void updateGame() {
        playerCamera.checkState();
        if (currentState == State.IN_GAME) {
            player.move(activeObjects);
            playerCamera.move(activeObjects);
        }
        mousePicker.update();
        renderer.updateDepthMaps(activeLights, player);
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

    public MenuCache getMenuCache() {
        return menuCache;
    }
}
