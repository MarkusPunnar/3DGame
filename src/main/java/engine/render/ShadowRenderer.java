package engine.render;

import engine.model.Model;
import engine.shader.Shader;
import engine.shader.ShadowShader;
import org.joml.Matrix4f;

import java.io.IOException;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ShadowRenderer extends EntityRenderer {

    private Shader shadowShader;

    public ShadowRenderer(Matrix4f projectionMatrix) throws IOException {
        super(projectionMatrix);
        shadowShader = new ShadowShader();
    }

    @Override
    public void bindModel(Model model) {
        glBindVertexArray(model.getModelID());
        glEnableVertexAttribArray(0);
    }

    @Override
    public Shader getShader() {
        return shadowShader;
    }
}
