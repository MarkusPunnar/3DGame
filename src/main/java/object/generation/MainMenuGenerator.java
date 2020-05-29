package object.generation;

import engine.DisplayManager;
import engine.loader.VAOLoader;
import game.ui.menu.Button;
import game.ui.menu.Menu;
import game.ui.UIComponent;
import game.ui.ObjectType;
import game.state.Game;
import game.ui.menu.MenuType;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMenuGenerator implements Generator {

    private VAOLoader loader;

    public MainMenuGenerator() {
        this.loader = Game.getInstance().getLoader();
    }

    @Override
    public List<UIComponent> generate() throws IOException {
        List<UIComponent> mainMenuComponents = new ArrayList<>();
        //add background image
        mainMenuComponents.add(new UIComponent(loader.loadObjectTexture("purple"), new Vector2f(), new Vector2f(1,1), ObjectType.BACKGROUND_IMAGE));
        //add title
        mainMenuComponents.add(new UIComponent(loader.loadGuiTexture("invtitle"), new Vector2f(0, 0.6f),
                new Vector2f(0.6f, 0.2f), ObjectType.INVENTORY_TITLE));
        //add main menu buttons
        mainMenuComponents.addAll(generateMainMenuButtons());
        return mainMenuComponents;
    }

    private List<Button> generateMainMenuButtons() throws IOException {
        float buttonWidth = 0.3f;
        float buttonHeight = 0.1f;
        Menu mainMenu = Game.getInstance().getMenuCache().getFromCache(MenuType.MAIN_MENU);
        Button newGameButton = new Button(loader.loadGuiTexture("invtitle"), new Vector2f(0, 0.15f),
                new Vector2f(buttonWidth, buttonHeight));
        newGameButton.setClickCallback(() -> {
            Game.getInstance().loadGame();
            return null;
        });
        mainMenu.addButton(newGameButton);
        Button loadGameButton = new Button(loader.loadGuiTexture("invtitle"), new Vector2f(0, -0.15f),
                new Vector2f(buttonWidth, buttonHeight));
        mainMenu.addButton(loadGameButton);
        Button optionsButton = new Button(loader.loadGuiTexture("invtitle"), new Vector2f(0, -0.45f),
                new Vector2f(buttonWidth, buttonHeight));
        mainMenu.addButton(optionsButton);
        Button quitButton = new Button(loader.loadGuiTexture("invtitle"), new Vector2f(0, -0.75f),
                new Vector2f(buttonWidth, buttonHeight));
        quitButton.setClickCallback(() -> {
            GLFW.glfwSetWindowShouldClose(DisplayManager.getWindow(), true);
            return null;
        });
        mainMenu.addButton(quitButton);
        return mainMenu.getButtons();
    }
}
