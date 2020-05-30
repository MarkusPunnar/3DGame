package game.ui.menu;

import engine.font.GUIText;
import game.ui.UIComponent;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Menu {

    private List<Button> buttons;
    private List<UIComponent> staticComponents;
    private List<GUIText> menuTexts;
    private boolean isInitialized;

    public Menu() {
        this.buttons = new ArrayList<>();
        this.staticComponents = new ArrayList<>();
        this.menuTexts = new ArrayList<>();
        this.isInitialized = false;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public List<UIComponent> getMenuComponents() {
        List<UIComponent> components = new ArrayList<>();
        components.addAll(staticComponents);
        components.addAll(buttons);
        return components;
    }

    public List<GUIText> getMenuTexts() {
        return menuTexts;
    }

    public void addStaticComponent(UIComponent component) {
        staticComponents.add(component);
    }

    public void addButton(Button button) {
        buttons.add(button);
    }

    public void addText(GUIText text) {
        menuTexts.add(text);
    }

    public Button getActiveButton(Vector2f mouseCoords) {
        Button activeButton = null;
        for (Button button : buttons) {
            if (button.contains(mouseCoords)) {
                activeButton = button;
                break;
            }
        }
        return activeButton;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }
}
