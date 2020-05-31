package game.object.item;

import game.ui.UIComponent;
import game.ui.ObjectType;
import game.state.Game;

import java.io.IOException;

public class Icon extends UIComponent {

    public Icon(String textureName) throws IOException {
        super(Game.getInstance().getLoader().loadIconTexture(textureName), null, null, ObjectType.ICON);
    }
}
