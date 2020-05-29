package engine.shader;

import com.google.common.flogger.FluentLogger;
import game.state.Game;
import object.env.Camera;
import object.env.Light;
import util.math.MathUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectionalShadowShader extends Shader {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static final String PREFIX = "shaders/shadow/directional/";

    private static final String VERTEX_FILE = PREFIX + "directionalShadowVertexShader.glsl";
    private static final String FRAGMENT_FILE = PREFIX + "directionalShadowFragmentShader.glsl";

    private Map<String, List<Integer>> uniformLocations;

    public DirectionalShadowShader() throws IOException {
        super(VERTEX_FILE, FRAGMENT_FILE, null);
        logger.atInfo().log("Initialized shader - %s", getClass().getSimpleName());
    }

    @Override
    public void loadUniforms(List<Light> lights) {
        doLoadMatrix(MathUtil.getLightSpaceMatrix(lights.get(0), Game.getInstance().getPlayerCamera()), "lightSpaceMatrix");
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
