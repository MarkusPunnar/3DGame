package engine.render;

import engine.font.GUIText;
import game.ui.ObjectType;
import game.object.RenderObject;
import org.joml.Vector2f;

public class RequestInfo {

    private Vector2f texturePosition;
    private Vector2f textureScale;
    private ObjectType guiType;
    private RenderObject object;
    private GUIText guiText;

    public RequestInfo(Vector2f texturePosition, Vector2f textureScale, ObjectType type) {
        this.texturePosition = texturePosition;
        this.textureScale = textureScale;
        this.guiType = type;
    }

    public RequestInfo(ObjectType type) {
       this(null, null, type);
    }

    public RequestInfo(RenderObject object, GUIText guiText) {
        this.object = object;
        this.guiText = guiText;
    }

    public Vector2f getTexturePosition() {
        return texturePosition;
    }

    public Vector2f getTextureScale() {
        return textureScale;
    }

    public ObjectType getGuiType() {
        return guiType;
    }

    public RenderObject getObject() {
        return object;
    }

    public GUIText getGuiText() {
        return guiText;
    }
}
