package engine.model;

import engine.shader.Shader;
import org.joml.Vector3f;

public class TextModel implements Model {

    private int modelID;
    private int vertexCount;
    private Vector3f colour;

    public TextModel(Vector3f colour) {
        this.colour = colour;
    }

    @Override
    public int getModelID() {
        return modelID;
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

    @Override
    public void prepareShader(Shader shader) {
        shader.doLoad3DVector(colour, "colour");
    }

    public RawModel getRawModel() {
        return null;
    }

    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }
}
