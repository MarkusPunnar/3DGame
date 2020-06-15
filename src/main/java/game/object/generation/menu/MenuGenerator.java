package game.object.generation.menu;

import game.ui.UIComponent;

import java.io.IOException;
import java.util.List;

public interface MenuGenerator {

    List<UIComponent> generate() throws IOException;
}
