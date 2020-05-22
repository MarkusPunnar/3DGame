package engine.shader;

import engine.model.Model;
import object.env.Camera;
import object.env.Light;
import util.math.MathUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticShader extends Shader {

    private static final String VERTEX_FILE = "shaders/vertexShader.glsl";
    private static final String FRAGMENT_FILE = "shaders/fragmentShader.glsl";

    private Map<String, List<Integer>> uniformLocations;

    public StaticShader() throws IOException {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    public void loadUniforms(List<Light> lights, Camera camera) {
        loadLights(lights, uniformLocations);
        doLoadMatrix(MathUtil.createViewMatrix(camera), "viewMatrix");
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "aPosition");
        bindAttribute(1, "aTextureCoords");
        bindAttribute(2, "aNormalCoords");
    }

    @Override
    protected void initUniformLocations() {
        if (uniformLocations == null) {
            uniformLocations = new HashMap<>();
        }
        uniformLocations.put("transformationMatrix", List.of(getUniformLocation("transformationMatrix")));
        uniformLocations.put("projectionMatrix", List.of(getUniformLocation("projectionMatrix")));
        uniformLocations.put("viewMatrix", List.of(getUniformLocation("viewMatrix")));
        uniformLocations.put("reflectivity", List.of(getUniformLocation("reflectivity")));
        uniformLocations.put("shineDamper", List.of(getUniformLocation("shineDamper")));
        uniformLocations.put("fakeLighting", List.of(getUniformLocation("fakeLighting")));
        List<Integer> lightPositions = new ArrayList<>();
        List<Integer> lightColours = new ArrayList<>();
        List<Integer> attenuations  = new ArrayList<>();
        for (int i = 0; i < MAX_LIGHTS; i++) {
            lightPositions.add(getUniformLocation("lightPosition[" + i + "]"));
            lightColours.add(getUniformLocation("lightColour[" + i + "]"));
            attenuations.add(getUniformLocation("attenuation[" + i + "]"));
        }
        uniformLocations.put("lightPosition", lightPositions);
        uniformLocations.put("lightColour", lightColours);
        uniformLocations.put("attenuation", attenuations);
    }

    @Override
    public Map<String, List<Integer>> getUniformLocations() {
        return uniformLocations;
    }

}
