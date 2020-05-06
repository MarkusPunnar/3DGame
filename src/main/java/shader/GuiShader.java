package shader;

import object.env.Light;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GuiShader extends ShaderProgram implements Shader {

    private static final String VERTEX_FILE = "shaders/guiVertexShader.glsl";
    private static final String FRAGMENT_FILE = "shaders/guiFragmentShader.glsl";

    private Map<String, Integer> uniformLocations;

    public GuiShader() throws IOException {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    public void doLoadMatrix(Matrix4f matrix, String uniformName) {
        Integer location = uniformLocations.get(uniformName);
        loadMatrix(location, matrix);
    }

    @Override
    public void loadSkyColour(float red, float green, float blue) {
    }

    @Override
    public void loadCameraPosition(Vector3f position) {
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "positionCoords");
    }

    @Override
    protected void getAllUniformLocations() {
        int transformationMatrixLocation = getUniformLocation("transformationMatrix");
        if (uniformLocations == null) {
            uniformLocations = new HashMap<>();
        }
        uniformLocations.put("transformationMatrix", transformationMatrixLocation);
    }

    @Override
    public void loadLight(Light light) {
    }
}
