package engine.model;

import engine.shader.Shader;

public class TerrainModel extends TexturedModel {


    public TerrainModel(RawModel rawModel, ModelTexture texture) {
        super(rawModel, texture);
    }

    @Override
    public void prepareShader(Shader shader) {
        shader.doLoadFloat(1, "reflectivity");
        shader.doLoadFloat(0, "shineDamper");
    }
}
