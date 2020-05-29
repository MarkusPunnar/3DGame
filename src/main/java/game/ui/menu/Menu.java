package game.ui.menu;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Menu {

    private List<Button> buttons;

    public Menu() {
        this.buttons = new ArrayList<>();
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void addButton(Button button) {
        buttons.add(button);
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
}
