package engine.shader;

import com.google.common.flogger.FluentLogger;
import game.state.Game;
import object.env.Camera;
import object.env.Light;
import util.math.MathUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityShader extends Shader {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static final String PREFIX = "shaders/entity/";

    private static final String VERTEX_FILE = PREFIX + "vertexShader.glsl";
    private static final String FRAGMENT_FILE = PREFIX + "fragmentShader.glsl";

    private Map<String, List<Integer>> uniformLocations;

    public EntityShader() throws IOException {
        super(VERTEX_FILE, FRAGMENT_FILE, null);
        logger.atInfo().log("Initialized shader - %s", getClass().getSimpleName());
    }

    @Override
    public void loadUniforms(List<Light> lights) {
        Camera camera = Game.getInstance().getPlayerCamera();
        doLoadMatrix(MathUtil.createViewMatrix(camera), "viewMatrix");
        doLoadMatrix(MathUtil.getLightSpaceMatrix(lights.get(0), camera), "lightSpaceMatrix");
        doLoadFloat(PointShadowShader.FAR_PLANE, "farPlane");
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
        uniformLocations.put("lightSpaceMatrix", List.of(getUniformLocation("lightSpaceMatrix")));
        uniformLocations.put("reflectivity", List.of(getUniformLocation("reflectivity")));
        uniformLocations.put("shineDamper", List.of(getUniformLocation("shineDamper")));
        uniformLocations.put("fakeLighting", List.of(getUniformLocation("fakeLighting")));
        uniformLocations.put("shadowMap", List.of(getUniformLocation("shadowMap")));
        uniformLocations.put("farPlane", List.of(getUniformLocation("farPlane")));
        List<Integer> lightPositions = new ArrayList<>();
        List<Integer> lightColours = new ArrayList<>();
        List<Integer> attenuations  = new ArrayList<>();
        List<Integer> shadowCubes = new ArrayList<>();
        for (int i = 0; i < MAX_LIGHTS; i++) {
            lightPositions.add(getUniformLocation("lightPosition[" + i + "]"));
            lightColours.add(getUniformLocation("lightColour[" + i + "]"));
            attenuations.add(getUniformLocation("attenuation[" + i + "]"));
        }
        for (int i = 0; i < MAX_LIGHTS - 1; i++) {
            shadowCubes.add(getUniformLocation("shadowCube[" + i + "]"));
        }
        uniformLocations.put("lightPosition", lightPositions);
        uniformLocations.put("lightColour", lightColours);
        uniformLocations.put("attenuation", attenuations);
        uniformLocations.put("shadowCube", shadowCubes);
    }

    @Override
    public Map<String, List<Integer>> getUniformLocations() {
        return uniformLocations;
    }

}
