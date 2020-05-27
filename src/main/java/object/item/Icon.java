package object.item;

import engine.texture.GuiTexture;
import engine.texture.ObjectType;
import game.state.Game;
import org.joml.Vector2f;

public class Icon extends GuiTexture {

    public Icon(int textureID, Vector2f position, Vector2f scale) {
        super(textureID, position, scale, ObjectType.ICON);
    }

    public Icon(String textureName) {
        super(Game.getInstance().getLoader().loadIconTexture(textureName), null, null, ObjectType.ICON);
    }
}
