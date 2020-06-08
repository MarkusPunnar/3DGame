package game.object.generation.menu;

import engine.DisplayManager;
import engine.loader.VAOLoader;
import game.ui.menu.Button;
import game.ui.menu.Menu;
import game.ui.UIComponent;
import game.ui.ObjectType;
import game.state.Game;
import game.ui.menu.MenuCache;
import game.ui.menu.MenuType;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import game.object.generation.GeneratorUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMenuGenerator implements MenuGenerator {

    private VAOLoader loader;
    private Menu mainMenu;

    public MainMenuGenerator() {
        this.loader = Game.getInstance().getLoader();
        MenuCache menuCache = Game.getInstance().getMenuCache();
        this.mainMenu = menuCache.getFromCache(MenuType.MAIN_MENU);
        if (mainMenu == null) {
            Menu mainMenu = new Menu();
            this.mainMenu = mainMenu;
            menuCache.addToCache(MenuType.MAIN_MENU, mainMenu);
        }
    }

    @Override
    public List<UIComponent> generate() throws IOException {
        List<UIComponent> mainMenuComponents = new ArrayList<>();
        //add background image
        UIComponent backgroundImage = new UIComponent(GeneratorUtil.getTextureFromCache("background"), new Vector2f(),
                new Vector2f(1, 1), ObjectType.BACKGROUND_IMAGE);
        mainMenu.addStaticComponent(backgroundImage);
        mainMenuComponents.add(backgroundImage);
        //add title
        UIComponent gameTitle = new UIComponent(loader.loadGuiTexture("invtitle"), new Vector2f(0, 0.6f),
                new Vector2f(0.6f, 0.2f), ObjectType.INVENTORY);
        mainMenu.addStaticComponent(gameTitle);
        mainMenuComponents.add(gameTitle);
        //add main menu buttons
        mainMenuComponents.addAll(generateMainMenuButtons());
        mainMenu.setInitialized(true);
        return mainMenuComponents;
    }

    private List<Button> generateMainMenuButtons() throws IOException {
        float buttonHeight = 0.2f;
        float buttonWidth = 0.2f;
        Button newGameButton = new Button(loader.loadGuiTexture("newgame"), new Vector2f(-0.75f, -0.5f),
                new Vector2f(buttonWidth, buttonHeight));
        newGameButton.setClickCallback(() -> {
            Game.getInstance().loadGame();
            return null;
        });
        mainMenu.addButton(newGameButton);
        Button loadGameButton = new Button(loader.loadGuiTexture("loadgame"), new Vector2f(-0.25f, -0.5f),
                new Vector2f(buttonWidth, buttonHeight));
        mainMenu.addButton(loadGameButton);
        Button optionsButton = new Button(loader.loadGuiTexture("options"), new Vector2f(0.25f, -0.5f),
                new Vector2f(buttonWidth, buttonHeight));
        optionsButton.setClickCallback(() -> {
            Game.getInstance().loadOptionsMenu();
            return null;
        });
        mainMenu.addButton(optionsButton);
        Button quitButton = new Button(loader.loadGuiTexture("quitgame"), new Vector2f(0.75f, -0.5f),
                new Vector2f(buttonWidth, buttonHeight));
        quitButton.setClickCallback(() -> {
            GLFW.glfwSetWindowShouldClose(DisplayManager.getWindow(), true);
            return null;
        });
        mainMenu.addButton(quitButton);
        return mainMenu.getButtons();
    }
}
