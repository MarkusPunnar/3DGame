package engine.render;

import engine.shader.PointShadowShader;
import engine.shader.Shader;
import game.object.env.Light;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.List;

public class PointShadowRenderer extends DirectionalShadowRenderer {

    private Shader pointShadowShader;

    public PointShadowRenderer(Matrix4f projectionMatrix) throws IOException {
        super(projectionMatrix);
        this.pointShadowShader = new PointShadowShader();
    }

    @Override
    public Shader getShader() {
        return pointShadowShader;
    }
}
