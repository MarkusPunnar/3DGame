package engine.model;

import engine.shader.Shader;

public interface Model {

    int getModelID();

    int getVertexCount();

    void prepareShader(Shader shader);

    RawModel getRawModel();
}
