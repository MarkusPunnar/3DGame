package engine.texture;

import engine.model.TexturedModel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import engine.render.RenderObject;

public class GuiTexture implements RenderObject {

    private int textureID;
    private Vector2f position;
    private Vector2f scale;
    private GuiType type;

    public GuiTexture(int textureID, Vector2f position, Vector2f scale, GuiType type) {
        this.textureID = textureID;
        this.position = position;
        this.scale = scale;
        this.type = type;
    }

    public GuiTexture() {
    }

    @Override
    public int getID() {
        return textureID;
    }

    public void setID(int textureID) {
        this.textureID = textureID;
    }

    @Override
    public TexturedModel getTexturedModel() {
        return null;
    }

    public Vector3f getPosition() {
        return new Vector3f(position, 0);
    }

    @Override
    public Vector3f getRotation() {
        return new Vector3f();
    }

    @Override
    public Vector3f getScaleVector() {
        return new Vector3f(scale, 1);
    }

    public GuiType getType() {
        return type;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
    }
}
