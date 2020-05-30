package engine.shader;

import com.google.common.flogger.FluentLogger;
import game.object.env.Light;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontShader extends Shader {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static final String PREFIX = "shaders/font/";

    private static final String VERTEX_FILE = PREFIX + "fontVertexShader.glsl";
    private static final String FRAGMENT_FILE = PREFIX + "fontFragmentShader.glsl";

    private Map<String, List<Integer>> uniformLocations;

    public FontShader() throws IOException {
        super(VERTEX_FILE, FRAGMENT_FILE, null);
        logger.atInfo().log("Initialized shader - %s", getClass().getSimpleName());
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "aPosition");
        bindAttribute(1, "aTextureCoords");
    }

    @Override
    protected void initUniformLocations() {
        if (uniformLocations == null) {
            uniformLocations = new HashMap<>();
        }
        uniformLocations.put("colour", List.of(getUniformLocation("colour")));
        uniformLocations.put("translation", List.of(getUniformLocation("translation")));
    }

    @Override
    public Map<String, List<Integer>> getUniformLocations() {
        return uniformLocations;
    }

    @Override
    public void loadUniforms(List<Light> lights) {

    }
}
