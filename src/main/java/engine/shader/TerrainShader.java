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
        int transformationMatrixLocation = getUniformLocation("transformationMatrix");
        int projectionMatrixLocation = getUniformLocation("projectionMatrix");
        int viewMatrixLocation = getUniformLocation("viewMatrix");
        int lightPositionLocation = getUniformLocation("lightPosition");
        int lightColourLocation = getUniformLocation("lightColour");
        int reflectivityLocation = getUniformLocation("reflectivity");
        int shineDamperLocation = getUniformLocation("shineDamper");
        if (uniformLocations == null) {
            uniformLocations = new HashMap<>();
        }
        uniformLocations.put("transformationMatrix", transformationMatrixLocation);
        uniformLocations.put("projectionMatrix", projectionMatrixLocation);
        uniformLocations.put("viewMatrix", viewMatrixLocation);
        uniformLocations.put("lightPosition", lightPositionLocation);
        uniformLocations.put("lightColour", lightColourLocation);
        uniformLocations.put("reflectivity", reflectivityLocation);
        uniformLocations.put("shineDamper", shineDamperLocation);
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
