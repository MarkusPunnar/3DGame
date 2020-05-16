package object.item;

import engine.loader.Loader;
import engine.texture.GuiTexture;
import engine.texture.ObjectType;
import org.joml.Vector2f;

public class Icon extends GuiTexture {

    public Icon(int textureID, Vector2f position, Vector2f scale) {
        super(textureID, position, scale, ObjectType.ICON);
    }

    public Icon(Loader loader, String textureName) {
        super(loader.loadIconTexture(textureName), null, null, ObjectType.ICON);
    }
}
