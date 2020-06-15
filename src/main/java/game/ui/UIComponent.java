package game.ui;

import com.google.common.flogger.FluentLogger;
import engine.font.GUIText;
import engine.model.Model;
import engine.shader.Shader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import game.object.RenderObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import util.math.MathUtil;

import java.util.Objects;

public class UIComponent extends RenderObject {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private int textureID;
    private Vector2f position;
    private Vector2f rotation;
    private Vector2f scale;
    private ObjectType type;
    private GUIText guiText;
    private boolean isTransparent;
    private float lifetime;

    public UIComponent(int textureID, Vector2f position, Vector2f scale, ObjectType type) {
        this(textureID, position, new Vector2f(), scale, type, Float.MAX_VALUE);
    }

    public UIComponent(int textureID, Vector2f position, Vector2f rotation, Vector2f scale, ObjectType type) {
        this(textureID, position, rotation, scale, type, Float.MAX_VALUE);
    }

    public UIComponent(int textureID, Vector2f position, Vector2f scale, ObjectType type, float lifetime) {
        this(textureID, position, new Vector2f(), scale, type, lifetime);
    }

    public UIComponent(int textureID, Vector2f position, Vector2f rotation, Vector2f scale, ObjectType type, float lifetime) {
        this.textureID = textureID;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.type = type;
        this.lifetime = lifetime;
    }

    @Override
    public int getID() {
        return textureID;
    }

    @Override
    public Model getModel() {
        return null;
    }

    public Vector3f getPosition() {
        return new Vector3f(position, 0);
    }

    @Override
    public Vector3f getRotation() {
        return new Vector3f(rotation, 0);
    }

    @Override
    public Vector3f getScaleVector() {
        return new Vector3f(scale, 1);
    }

    @Override
    public void prepareObject(Shader shader) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        shader.doLoadBoolean(isTransparent, "transparent");
        shader.doLoadMatrix(MathUtil.createTransformationMatrix(getPosition(), getRotation(),  getScaleVector()), "transformationMatrix");
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
                Objects.equals(position, that.position) &&
                Objects.equals(scale, that.scale) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(textureID, position, scale, type);
    }

    public GUIText getGuiText() {
        return guiText;
    }

    public void setGuiText(GUIText guiText) {
        this.guiText = guiText;
    }

    public void setTransparent(boolean transparent) {
        isTransparent = transparent;
    }

    public float getLifetime() {
        return lifetime;
    }

    public void decreaseLifetime(float time) {
        this.lifetime -= time;
    }
}
