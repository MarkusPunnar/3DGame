package engine.render;

import engine.texture.GuiType;
import org.joml.Vector2f;

public class RequestInfo {

    private Vector2f texturePosition;
    private Vector2f textureScale;
    private GuiType guiType;
    private RenderObject object;

    public RequestInfo(Vector2f texturePosition, Vector2f textureScale, GuiType type) {
        this.texturePosition = texturePosition;
        this.textureScale = textureScale;
        this.guiType = type;
    }

    public RequestInfo(GuiType type) {
       this(null, null, type);
    }

    public RequestInfo(RenderObject object) {
        this.object = object;
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

    public RenderObject getObject() {
        return object;
    }
}
