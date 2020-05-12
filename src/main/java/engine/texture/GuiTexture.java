package engine.texture;

import engine.model.TexturedModel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import engine.render.RenderObject;

import java.util.Objects;

public class GuiTexture implements RenderObject {

    private static int INCREMENT = 0;

    private int textureID;
    private Vector2f position;
    private Vector2f scale;
    private GuiType type;
    private int priority;

    public GuiTexture(int textureID, Vector2f position, Vector2f scale, GuiType type) {
        this.textureID = textureID;
        this.position = position;
        this.scale = scale;
        this.type = type;
        this.priority = INCREMENT;
        if (type == GuiType.ICON) {
            this.priority += 1000;
        }
        INCREMENT++;
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

    public void setType(GuiType type) {
        this.type = type;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        GuiTexture that = (GuiTexture) other;
        return textureID == that.textureID &&
                priority == that.priority &&
                Objects.equals(position, that.position) &&
                Objects.equals(scale, that.scale) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(textureID, position, scale, type, priority);
    }
}
