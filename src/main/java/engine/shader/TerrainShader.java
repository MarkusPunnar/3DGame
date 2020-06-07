package engine.shader;

import com.google.common.flogger.FluentLogger;
import game.state.Game;
import game.object.env.Camera;
import game.object.env.Light;
import util.math.MathUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerrainShader extends Shader {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static final String PREFIX = "shaders/terrain/";

    private static final String VERTEX_FILE = PREFIX + "terrainVertexShader.glsl";
    private static final String FRAGMENT_FILE = PREFIX + "terrainFragmentShader.glsl";

    private Map<String, List<Integer>> uniformLocations;

    public TerrainShader() throws IOException {
        super(VERTEX_FILE, FRAGMENT_FILE, null);
        logger.atInfo().log("Initialized shader - %s", getClass().getSimpleName());
    }


    @Override
    public void loadUniforms(List<Light> lights) {
        Camera camera = Game.getInstance().getPlayerCamera();
        loadLights(lights, uniformLocations);
        doLoadMatrix(MathUtil.createViewMatrix(camera), "viewMatrix");
        doLoadMatrix(MathUtil.getLightSpaceMatrix(shadowBox), "lightSpaceMatrix");
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
        uniformLocations.put("backgroundSampler", List.of(getUniformLocation("backgroundSampler")));
        uniformLocations.put("rSampler", List.of(getUniformLocation("rSampler")));
        uniformLocations.put("gSampler", List.of(getUniformLocation("gSampler")));
        uniformLocations.put("bSampler", List.of(getUniformLocation("bSampler")));
        uniformLocations.put("blendMapSampler", List.of(getUniformLocation("blendMapSampler")));
        uniformLocations.put("shadowMap", List.of(getUniformLocation("shadowMap")));
        List<Integer> lightPositions = new ArrayList<>();
        List<Integer> lightColours = new ArrayList<>();
        List<Integer> attenuations = new ArrayList<>();
        for (int i = 0; i < MAX_LIGHTS; i++) {
            lightPositions.add(getUniformLocation("lightPosition[" + i + "]"));
            lightColours.add(getUniformLocation("lightColour[" + i + "]"));
            attenuations.add(getUniformLocation("attenuation[" + i + "]"));
        }
        uniformLocations.put("lightPosition", lightPositions);
        uniformLocations.put("lightColour", lightColours);
        uniformLocations.put("attenuation", attenuations);
    }

    public void connectTextureUnits() {
        loadInt(uniformLocations.get("backgroundSampler").get(0), 0);
        loadInt(uniformLocations.get("rSampler").get(0), 1);
        loadInt(uniformLocations.get("gSampler").get(0), 2);
        loadInt(uniformLocations.get("bSampler").get(0), 3);
        loadInt(uniformLocations.get("blendMapSampler").get(0), 4);
        loadInt(uniformLocations.get("shadowMap").get(0), 5);
    }

    @Override
    public Map<String, List<Integer>> getUniformLocations() {
        return uniformLocations;
    }

}
