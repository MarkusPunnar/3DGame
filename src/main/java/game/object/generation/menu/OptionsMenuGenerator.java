package game.object.generation.menu;

import engine.font.GUIText;
import engine.loader.VAOLoader;
import engine.render.RenderRequest;
import engine.render.RequestInfo;
import engine.render.RequestType;
import game.config.Config;
import game.state.Game;
import game.state.HandlerState;
import game.ui.ObjectType;
import game.ui.UIComponent;
import game.ui.menu.Button;
import game.ui.menu.Menu;
import game.ui.menu.MenuCache;
import game.ui.menu.MenuType;
import org.joml.Vector2f;
import util.GeneratorUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OptionsMenuGenerator implements MenuGenerator {

    private VAOLoader loader;
    private Menu optionsMenu;

    public OptionsMenuGenerator() {
        this.loader = Game.getInstance().getLoader();
        MenuCache menuCache = Game.getInstance().getMenuCache();
        this.optionsMenu = menuCache.getFromCache(MenuType.OPTIONS_MENU);
        if (optionsMenu == null) {
            Menu optionsMenu = new Menu();
            this.optionsMenu = optionsMenu;
            menuCache.addToCache(MenuType.OPTIONS_MENU, optionsMenu);
        }
    }

    @Override
    public List<UIComponent> generate() throws IOException {
        List<UIComponent> optionsMenuComponents = new ArrayList<>();
        UIComponent backgroundImage = new UIComponent(loader.loadGuiTexture("background"),
                new Vector2f(), new Vector2f(1, 1), ObjectType.BACKGROUND_IMAGE);
        optionsMenu.addStaticComponent(backgroundImage);
        optionsMenuComponents.add(backgroundImage);
        //add options area background
        UIComponent optionsArea = new UIComponent(loader.loadGuiTexture("slot"), new Vector2f(0, -0.3f),
                new Vector2f(0.7f, 0.7f), ObjectType.BACKGROUND_IMAGE);
        optionsMenu.addStaticComponent(optionsArea);
        optionsMenuComponents.add(optionsArea);
        //add buttons
        optionsMenuComponents.addAll(generateOptions());
        //add text
        optionsMenu.setInitialized(true);
        return optionsMenuComponents;
    }

    private List<Button> generateOptions() throws IOException {
        List<Button> optionButtons = new ArrayList<>();
        //Add return to main menu button
        Button returnButton = new Button(loader.loadGuiTexture("invtitle"), new Vector2f(0.5f, -0.8f),
                new Vector2f(0.1f, 0.05f));
        returnButton.setClickCallback(() -> {
            Game.getInstance().loadMainMenu();
            return null;
        });
        optionsMenu.addButton(returnButton);
        optionButtons.add(returnButton);
        //Generate texts
        GUIText shadowQuality = new GUIText.Builder("Shadow Quality")
                .position(GeneratorUtil.fromOpenGLCoords(-0.6f, 0.3f)).fontSize(0.9f)
                .lineLength(0.3f).centered(true).build();
        GUIText shadowLevel = new GUIText.Builder(Config.getInstance().getShadowLevel().getLevelAsString())
                .position(GeneratorUtil.fromOpenGLCoords(-0.6f, 0.15f)).fontSize(0.6f)
                .lineLength(0.3f).centered(true).build();
        String invertedMouseText = Config.getInstance().getInvertedMouse() == 1 ? "Off" : "On";
        GUIText invertedMouse = new GUIText.Builder("Inverted Mouse").position(GeneratorUtil.fromOpenGLCoords(0f, 0.3f))
                .fontSize(0.9f).lineLength(0.3f).centered(true).build();
        GUIText invertedMouseOption = new GUIText.Builder(invertedMouseText).position(GeneratorUtil.fromOpenGLCoords(0f, 0.15f))
                .fontSize(0.6f).lineLength(0.3f).centered(true).build();
        optionsMenu.addText(shadowLevel);
        optionsMenu.addText(shadowQuality);
        optionsMenu.addText(invertedMouseOption);
        optionsMenu.addText(invertedMouse);
        //Add shadow quality increase button
        Button increaseShadowQualityButton = new Button(loader.loadGuiTexture("arrow"), new Vector2f(-0.15f, 0.12f),
                new Vector2f(0.025f, 0.03f));
        increaseShadowQualityButton.setClickCallback(() -> {
            Config.getInstance().changeShadowLevel(1);
            updateText(increaseShadowQualityButton, Config.getInstance().getShadowLevel().getLevelAsString());
            return null;
        });
        optionButtons.add(increaseShadowQualityButton);
        increaseShadowQualityButton.setGuiText(shadowLevel);
        optionsMenu.addButton(increaseShadowQualityButton);
        //Add shadow quality decrease button
        Button decreaseShadowQualityButton = new Button(loader.loadGuiTexture("arrow"), new Vector2f(-0.46f, 0.12f),
                new Vector2f(0, 180), new Vector2f(0.025f, 0.03f));
        decreaseShadowQualityButton.setClickCallback(() -> {
            Config.getInstance().changeShadowLevel(-1);
            updateText(decreaseShadowQualityButton, Config.getInstance().getShadowLevel().getLevelAsString());
            return null;
        });
        optionButtons.add(decreaseShadowQualityButton);
        decreaseShadowQualityButton.setGuiText(shadowLevel);
        optionsMenu.addButton(decreaseShadowQualityButton);
        //Add inverted mouse control button
        Button invertedMouseButton = new Button(loader.loadGuiTexture("arrow"), new Vector2f(0.4f, 0.12f),
                new Vector2f(0.025f, 0.03f));
        invertedMouseButton.setClickCallback(() -> {
            Config config = Config.getInstance();
            config.setInvertedMouse(config.getInvertedMouse() * (-1));
            String newText = config.getInvertedMouse() == 1 ? "Off" : "On";
            updateText(invertedMouseButton, newText);
            return null;
        });
        optionButtons.add(invertedMouseButton);
        invertedMouseButton.setGuiText(invertedMouseOption);
        optionsMenu.addButton(invertedMouseButton);
        return optionButtons;

    }

    private void updateText(Button button, String newValue) {
        GUIText text = button.getGuiText();
        if (text != null) {
            HandlerState.getInstance().registerRequest(new RenderRequest(RequestType.REMOVE_TEXT, new RequestInfo(null, text)));
            GUIText newText = text.copyWithValueChange(newValue);
            HandlerState.getInstance().registerRequest(new RenderRequest(RequestType.REFRESH_TEXT, new RequestInfo(null, newText)));
            optionsMenu.getMenuTexts().remove(text);
            optionsMenu.addText(newText);
        }
    }
}
