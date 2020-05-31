package engine.shader;

import com.google.common.flogger.FluentLogger;
import game.object.env.Light;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiShader extends Shader {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static final String PREFIX = "shaders/gui/";

    private static final String VERTEX_FILE = PREFIX + "guiVertexShader.glsl";
    private static final String FRAGMENT_FILE = PREFIX + "guiFragmentShader.glsl";

    private Map<String, List<Integer>> uniformLocations;

    public GuiShader() throws IOException {
        super(VERTEX_FILE, FRAGMENT_FILE, null);
        logger.atInfo().log("Initialized shader - %s", getClass().getSimpleName());
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "positionCoords");
    }

    @Override
    protected void initUniformLocations() {
        if (uniformLocations == null) {
            uniformLocations = new HashMap<>();
        }
        uniformLocations.put("transformationMatrix", List.of(getUniformLocation("transformationMatrix")));
        uniformLocations.put("transparent", List.of(getUniformLocation("transparent")));
    }

    @Override
    public Map<String, List<Integer>> getUniformLocations() {
        return uniformLocations;
    }

    @Override
    public void loadUniforms(List<Light> lights) {

    }
}
