package game.object.generation.menu;

import game.ui.UIComponent;
import game.object.generation.Generator;

import java.io.IOException;
import java.util.List;

public interface MenuGenerator extends Generator {

    @Override
    List<UIComponent> generate() throws IOException;
}
