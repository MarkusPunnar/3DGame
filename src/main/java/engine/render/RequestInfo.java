package engine.render;

import engine.texture.GuiType;
import org.joml.Vector2f;

public class RequestInfo {

    private Vector2f texturePosition;
    private Vector2f textureScale;
    private GuiType guiType;

    public RequestInfo(Vector2f texturePosition, Vector2f textureScale, GuiType type) {
        this.texturePosition = texturePosition;
        this.textureScale = textureScale;
        this.guiType = type;
    }

    public RequestInfo(GuiType type) {
       this(null, null, type);
    }

    public Vector2f getTexturePosition() {
        return texturePosition;
    }

    public Vector2f getTextureScale() {
        return textureScale;
    }

    public GuiType getGuiType() {
        return guiType;
    }
}
