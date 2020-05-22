package engine.shader;

import engine.model.Model;
import object.env.Camera;
import object.env.Light;
import util.math.MathUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShadowShader extends Shader {

    private static final String VERTEX_FILE = "shaders/shadowVertexShader.glsl";
    private static final String FRAGMENT_FILE = "shaders/shadowFragmentShader.glsl";

    private Map<String, List<Integer>> uniformLocations;

    public ShadowShader() throws IOException {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    public void loadUniforms(List<Light> lights, Camera camera) {
        doLoadMatrix(MathUtil.getLightSpaceMatrix(lights.get(0), camera), "lightSpaceMatrix");
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "aPosition");
    }

    @Override
    protected void initUniformLocations() {
        if (uniformLocations == null) {
            uniformLocations = new HashMap<>();
        }
        uniformLocations.put("transformationMatrix", List.of(getUniformLocation("transformationMatrix")));
        uniformLocations.put("lightSpaceMatrix", List.of(getUniformLocation("lightSpaceMatrix")));
    }

    @Override
    public Map<String, List<Integer>> getUniformLocations() {
        return uniformLocations;
    }

}
