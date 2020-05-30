package game.object;

import com.google.common.flogger.FluentLogger;
import engine.model.TexturedModel;
import engine.shader.Shader;
import game.ui.ObjectType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import util.GeneratorUtil;
import util.math.MathUtil;

import java.util.Objects;

public class Entity extends RenderObject {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private TexturedModel texturedModel;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scaleVector;

    protected Entity(Builder builder) {
        this.texturedModel = builder.texturedModel;
        this.position = builder.position;
        this.rotation = builder.rotation;
        this.scaleVector = builder.scaleVector;
        GeneratorUtil.setParentObject(this);
    }

    public static class Builder extends RenderObject.Builder {

        private final TexturedModel texturedModel;
        private final Vector3f position;

        private Vector3f rotation = new Vector3f();
        private Vector3f scaleVector = new Vector3f(1);

        public Builder(TexturedModel texturedModel, Vector3f position) {
            this.texturedModel = texturedModel;
            this.position = position;
        }

        public Builder rotation(Vector3f rotation) {
            this.rotation = rotation;
            return self();
        }

        public Builder rotationY(float rotationY) {
            this.rotation.y = rotationY;
            return self();
        }

        public Builder scale(Vector3f scale) {
            this.scaleVector = scale;
            return self();
        }

        public Builder scaleY(float scaleY) {
            this.scaleVector.y = scaleY;
            return self();
        }

        public Entity build() {
            return new Entity(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    public void increasePosition(float dx, float dy, float dz) {
        position.x += dx;
        position.y += dy;
        position.z += dz;
        logger.atInfo().log("Changed %s position by (%f, %f, %f)", getClass().getSimpleName(), dx, dy, dz);
    }

    public void increaseRotation(float rx, float ry, float rz) {
        rotation.x += rx;
        rotation.y += ry;
        rotation.z += rz;
        logger.atInfo().log("Changed %s rotation by (%f, %f, %f)", getClass().getSimpleName(), rx, ry, rz);
    }

    public TexturedModel getModel() {
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

    public ObjectType getType() {
        return ObjectType.ENTITY;
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
