package engine.shader;

import object.env.Light;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StaticShader extends ShaderProgram implements Shader {

    private static final String VERTEX_FILE = "shaders/vertexShader.glsl";
    private static final String FRAGMENT_FILE = "shaders/fragmentShader.glsl";

    private Map<String, Integer> uniformLocations;

    public StaticShader() throws IOException {
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
        uniformLocations.put("fakeLighting", getUniformLocation("fakeLighting"));
    }

    public void loadFakeLighting(boolean fakeLighting) {
        Integer fakeLightingLocation = uniformLocations.get("fakeLighting");
        loadBoolean(fakeLightingLocation, fakeLighting);
    }

    public void loadLight(Light light) {
        Integer positionLocation = uniformLocations.get("lightPosition");
        Integer colourLocation = uniformLocations.get("lightColour");
        loadVector(positionLocation, light.getPosition());
        loadVector(colourLocation, light.getColour());
    }

    public void loadShineVariables(float reflectivity, float shineDamper) {
        Integer reflectivityPosition = uniformLocations.get("reflectivity");
        Integer shineDamperLocation = uniformLocations.get("shineDamper");
        loadFloat(reflectivityPosition, reflectivity);
        loadFloat(shineDamperLocation, shineDamper);
    }

    public void doLoadMatrix(Matrix4f matrix, String uniformName) {
        Integer location = uniformLocations.get(uniformName);
        loadMatrix(location, matrix);
    }
}
