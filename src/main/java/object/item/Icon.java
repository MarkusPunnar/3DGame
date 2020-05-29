package object.item;

import engine.texture.GuiTexture;
import engine.texture.ObjectType;
import game.state.Game;
import org.joml.Vector2f;

import java.io.IOException;

public class Icon extends GuiTexture {

    public Icon(int textureID, Vector2f position, Vector2f scale) {
        super(textureID, position, scale, ObjectType.ICON);
    }

    public Icon(String textureName) throws IOException {
        super(Game.getInstance().getLoader().loadIconTexture(textureName), null, null, ObjectType.ICON);
    }
}
