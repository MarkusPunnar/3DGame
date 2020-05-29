package game.ui;

import com.google.common.flogger.FluentLogger;
import engine.font.GUIText;
import engine.model.TexturedModel;
import engine.shader.Shader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import object.RenderObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import util.math.MathUtil;

import java.util.Objects;

public class UIComponent extends RenderObject {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static int INCREMENT = 0;

    private int textureID;
    private Vector2f position;
    private Vector2f scale;
    private ObjectType type;
    private int priority;
    private GUIText guiText;

    public UIComponent(int textureID, Vector2f position, Vector2f scale, ObjectType type) {
        this.textureID = textureID;
        this.position = position;
        this.scale = scale;
        this.type = type;
        this.priority = INCREMENT;
        if (type == ObjectType.ICON) {
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
    public TexturedModel getModel() {
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

    @Override
    public void prepareObject(Shader shader) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        shader.doLoadMatrix(MathUtil.createTransformationMatrix(getPosition(), getScaleVector()), "transformationMatrix");
    }

    public ObjectType getType() {
        return type;
    }

    public void setType(ObjectType type) {
        this.type = type;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
    }

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
        UIComponent that = (UIComponent) other;
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

    public GUIText getGuiText() {
        return guiText;
    }

    public void setGuiText(GUIText guiText) {
        this.guiText = guiText;
    }
}
