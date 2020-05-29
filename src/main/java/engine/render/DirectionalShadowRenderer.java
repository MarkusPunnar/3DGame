package engine.render;

import engine.model.Model;
import engine.shader.Shader;
import engine.shader.DirectionalShadowShader;
import object.env.Light;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.List;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class DirectionalShadowRenderer extends EntityRenderer {

    private Shader shadowShader;

    public DirectionalShadowRenderer(Matrix4f projectionMatrix, List<Light> lights) throws IOException {
        super(projectionMatrix, lights);
        shadowShader = new DirectionalShadowShader();
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
