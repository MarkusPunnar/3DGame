package object;

import engine.model.TexturedModel;
import org.joml.Vector3f;
import engine.render.RenderObject;

import java.util.Objects;

public class Entity implements RenderObject {

    private TexturedModel texturedModel;
    private Vector3f position;
    private float rotationX;
    private float rotationY;
    private float rotationZ;
    private Vector3f scaleVector;

    public Entity(TexturedModel texturedModel, Vector3f position, float rotationX, float rotationY, float rotationZ, Vector3f scaleVector) {
        this.texturedModel = texturedModel;
        this.position = position;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        this.scaleVector = scaleVector;
    }

    public void increasePosition(float dx, float dy, float dz) {
        position.x += dx;
        position.y += dy;
        position.z += dz;
    }

    public void increaseRotation(float rx, float ry, float rz) {
        rotationX += rx;
        rotationY += ry;
        rotationZ += rz;
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

    public float getRotationX() {
        return rotationX;
    }

    public void setRotationX(float rotationX) {
        this.rotationX = rotationX;
    }

    public float getRotationY() {
        return rotationY;
    }

    public void setRotationY(float rotationY) {
        this.rotationY = rotationY;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public void setRotationZ(float rotationZ) {
        this.rotationZ = rotationZ;
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
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Entity entity = (Entity) other;
        return Float.compare(entity.rotationX, rotationX) == 0 &&
                Float.compare(entity.rotationY, rotationY) == 0 &&
                Float.compare(entity.rotationZ, rotationZ) == 0 &&
                scaleVector.equals(entity.getScaleVector()) &&
                texturedModel.equals(entity.texturedModel) &&
                position.equals(entity.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texturedModel, position, rotationX, rotationY, rotationZ, scaleVector);
    }
}
