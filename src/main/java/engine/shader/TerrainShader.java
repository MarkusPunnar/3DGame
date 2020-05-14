package engine.shader;

import object.env.Light;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TerrainShader extends ShaderProgram implements Shader {

    private static final String VERTEX_FILE = "shaders/terrainVertexShader.glsl";
    private static final String FRAGMENT_FILE = "shaders/terrainFragmentShader.glsl";

    private Map<String, Integer> uniformLocations;

    public TerrainShader() throws IOException {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "aPosition");
        bindAttribute(1, "aTextureCoords");
        bindAttribute(2, "aNormalCoords");
    }

    @Override
    protected void getAllUniformLocations() {
        if (uniformLocations == null) {
            uniformLocations = new HashMap<>();
        }
        uniformLocations.put("transformationMatrix", getUniformLocation("transformationMatrix"));
        uniformLocations.put("projectionMatrix", getUniformLocation("projectionMatrix"));
        uniformLocations.put("viewMatrix", getUniformLocation("viewMatrix"));
        uniformLocations.put("lightPosition", getUniformLocation("lightPosition"));
        uniformLocations.put("lightColour", getUniformLocation("lightColour"));
        uniformLocations.put("reflectivity", getUniformLocation("reflectivity"));
        uniformLocations.put("shineDamper", getUniformLocation("shineDamper"));
        uniformLocations.put("backgroundSampler", getUniformLocation("backgroundSampler"));
        uniformLocations.put("rSampler", getUniformLocation("rSampler"));
        uniformLocations.put("gSampler", getUniformLocation("gSampler"));
        uniformLocations.put("bSampler", getUniformLocation("bSampler"));
        uniformLocations.put("blendMapSampler", getUniformLocation("blendMapSampler"));
    }

    public void connectTextureUnits() {
        loadInt(uniformLocations.get("backgroundSampler"), 0);
        loadInt(uniformLocations.get("rSampler"), 1);
        loadInt(uniformLocations.get("gSampler"), 2);
        loadInt(uniformLocations.get("bSampler"), 3);
        loadInt(uniformLocations.get("blendMapSampler"), 4);
    }

    public void loadLight(Light light) {
        loadVector(uniformLocations.get("lightPosition"), light.getPosition());
        loadVector(uniformLocations.get("lightColour"), light.getColour());
    }

    public void loadShineVariables(float reflectivity, float shineDamper) {
        loadFloat(uniformLocations.get("reflectivity"), reflectivity);
        loadFloat(uniformLocations.get("shineDamper"), shineDamper);
    }

    public void doLoadMatrix(Matrix4f matrix, String uniformName) {
        loadMatrix(uniformLocations.get(uniformName), matrix);
    }
}
