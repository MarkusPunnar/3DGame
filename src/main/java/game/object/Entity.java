package game.object;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.flogger.FluentLogger;
import engine.model.TexturedModel;
import engine.shader.Shader;
import game.object.generation.GenerationUtil;
import game.ui.ObjectType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import game.object.generation.EntityLoader;
import util.math.MathUtil;
import util.math.structure.Triangle;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Entity extends RenderObject {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private TexturedModel texturedModel;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scaleVector;
    private List<Triangle> triangles;

    protected Entity(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scaleVector) {
        this.texturedModel = model;
        this.position = position;
        this.rotation = rotation;
        this.scaleVector = scaleVector;
        this.triangles = texturedModel.getRawModel().createTrianglesFromBox();
        GenerationUtil.setParentObject(this);
    }

    public static Entity build(TexturedModel model, JsonNode attributeNode, Map<String, Vector3f> objectData) {
        JsonNode positionNode = attributeNode.get("position");
        Vector3f rotation = attributeNode.has("rotation") ? EntityLoader.getVectorFromNode(attributeNode.get("rotation")) : new Vector3f();
        Vector3f scale = objectData.containsKey("scale") ? objectData.get("scale") : new Vector3f(1);
        return new Entity(model, EntityLoader.getVectorFromNode(positionNode), rotation, scale);
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
    public List<Triangle> getTriangles() {
        return triangles;
    }

    public void setTriangles(List<Triangle> triangles) {
        this.triangles = triangles;
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
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Entity entity = (Entity) other;
        return rotation.equals(entity.getRotation()) &&
                scaleVector.equals(entity.getScaleVector()) &&
                texturedModel.equals(entity.texturedModel) &&
                position.equals(entity.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texturedModel, position, rotation, scaleVector);
    }
}
