package object;

import engine.model.TexturedModel;
import engine.shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import engine.render.RenderObject;
import util.math.MathUtil;

import java.util.Objects;

public class Entity implements RenderObject {

    private TexturedModel texturedModel;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scaleVector;

    public Entity(TexturedModel texturedModel, Vector3f position, Vector3f rotation, Vector3f scaleVector) {
        this.texturedModel = texturedModel;
        this.position = position;
        this.rotation = rotation;
        this.scaleVector = scaleVector;
    }

    public void increasePosition(float dx, float dy, float dz) {
        position.x += dx;
        position.y += dy;
        position.z += dz;
    }

    public void increaseRotation(float rx, float ry, float rz) {
        rotation.x += rx;
        rotation.y += ry;
        rotation.z += rz;
    }

    public TexturedModel getTexturedModel() {
        return texturedModel;
    }

    public void setTexturedModel(TexturedModel texturedModel) {
        this.texturedModel = texturedModel;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScaleVector() {
        return scaleVector;
    }

    public void setScaleVector(Vector3f scaleVector) {
        this.scaleVector = scaleVector;
    }

    public int getID() {
        return texturedModel.getModelID();
    }

    @Override
    public void prepareObject(Shader shader) {
        Matrix4f transformationMatrix = MathUtil.createTransformationMatrix(getPosition(), getRotation(), getScaleVector());
        shader.doLoadMatrix(transformationMatrix, "transformationMatrix");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Entity entity = (Entity) other;
        return  rotation.equals(entity.getRotation()) &&
                scaleVector.equals(entity.getScaleVector()) &&
                texturedModel.equals(entity.texturedModel) &&
                position.equals(entity.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texturedModel, position, rotation, scaleVector);
    }
}
