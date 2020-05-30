package engine.shader;

import com.google.common.flogger.FluentLogger;
import game.object.env.Light;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PointShadowShader extends Shader {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static final String PREFIX = "shaders/shadow/point/";

    private static final String VERTEX_FILE = PREFIX + "pointShadowVertexShader.glsl";
    private static final String FRAGMENT_FILE = PREFIX + "pointShadowFragmentShader.glsl";
    private static final String GEOMETRY_FILE = PREFIX + "pointShadowGeometryShader.glsl";

    protected static final float NEAR_PLANE = 0.1f;
    protected static final float FAR_PLANE = 100f;

    private Map<String, List<Integer>> uniformLocations;

    public PointShadowShader() throws IOException {
        super(VERTEX_FILE, FRAGMENT_FILE, GEOMETRY_FILE);
        logger.atInfo().log("Initialized shader - %s", getClass().getSimpleName());
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
        uniformLocations.put("lightCoords", List.of(getUniformLocation("lightCoords")));
        uniformLocations.put("farPlane", List.of(getUniformLocation("farPlane")));
        List<Integer> lightMatrices = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lightMatrices.add(getUniformLocation("lightMatrices[" + i + "]"));
        }
        uniformLocations.put("lightMatrices", lightMatrices);
    }

    @Override
    public Map<String, List<Integer>> getUniformLocations() {
        return uniformLocations;
    }

    @Override
    public void loadUniforms(List<Light> lights) {
        Matrix4f shadowProjectionMatrix = new Matrix4f().perspective(((float) Math.toRadians(90)), 1, NEAR_PLANE, FAR_PLANE);
        loadLightMatrices(shadowProjectionMatrix, lights.get(0));
        doLoadFloat(FAR_PLANE, "farPlane");
        doLoad3DVector(lights.get(0).getPosition(), "lightCoords");
    }

    private void loadLightMatrices(Matrix4f shadowProjectionMatrix, Light light) {
        List<Integer> lightMatrixPositions = uniformLocations.get("lightMatrices");
        loadLightMatrix(shadowProjectionMatrix, light, new Vector3f(1, 0, 0), new Vector3f(0, -1, 0), lightMatrixPositions.get(0));
        loadLightMatrix(shadowProjectionMatrix, light, new Vector3f(-1, 0, 0), new Vector3f(0, -1, 0), lightMatrixPositions.get(1));
        loadLightMatrix(shadowProjectionMatrix, light, new Vector3f(0, 1, 0), new Vector3f(0, 0, 1), lightMatrixPositions.get(2));
        loadLightMatrix(shadowProjectionMatrix, light, new Vector3f(0, -1, 0), new Vector3f(0, 0, -1), lightMatrixPositions.get(3));
        loadLightMatrix(shadowProjectionMatrix, light, new Vector3f(0, 0, 1), new Vector3f(0, -1, 0), lightMatrixPositions.get(4));
        loadLightMatrix(shadowProjectionMatrix, light, new Vector3f(0, 0, -1), new Vector3f(0, -1, 0), lightMatrixPositions.get(5));
    }

    private void loadLightMatrix(Matrix4f shadowProjectionMatrix, Light light, Vector3f offset, Vector3f upDirection, int position) {
        Matrix4f lightMatrix = new Matrix4f().lookAt(light.getPosition(), light.getPosition().add(offset, new Vector3f()), upDirection);
        loadMatrix(position, shadowProjectionMatrix.mul(lightMatrix, new Matrix4f()));
    }
}
