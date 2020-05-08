package object.item;

import engine.loader.Loader;
import engine.texture.GuiTexture;
import engine.texture.GuiType;
import org.joml.Vector2f;

public class Icon extends GuiTexture {

    public Icon(int textureID, Vector2f position, Vector2f scale) {
        super(textureID, position, scale, GuiType.ICON);
    }

    public Icon(Loader loader, String textureName) {
        super(loader.loadTexture(textureName), null, null, GuiType.ICON);
    }
}
