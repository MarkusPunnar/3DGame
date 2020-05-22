package engine.shader;

import engine.model.Model;
import object.env.Camera;
import object.env.Light;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

public abstract class Shader {

    protected final int MAX_LIGHTS = 10;

    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    public Shader(String vertexFile, String fragmentFile) throws IOException {
        vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
        programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        glLinkProgram(programID);
        glValidateProgram(programID);
        initUniformLocations();
    }


    public void start() {
        glUseProgram(programID);
    }

    public void stop() {
        glUseProgram(0);
    }

    public void cleanUp() {
        stop();
        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
    }

    protected abstract void bindAttributes();

    protected abstract void initUniformLocations();

    public abstract Map<String, List<Integer>> getUniformLocations();

    public abstract void loadUniforms(List<Light> lights, Camera camera);


    public void doLoadMatrix(Matrix4f matrix, String uniformName) {
        loadMatrix(getUniformLocations().get(uniformName).get(0), matrix);
    }

    public void doLoad3DVector(Vector3f vector, String uniformName) {
        load3DVector(getUniformLocations().get(uniformName).get(0), vector);
    }

    public void doLoad2DVector(Vector2f vector, String uniformName) {
        load2DVector(getUniformLocations().get(uniformName).get(0), vector);
    }

    public void doLoadFloat(float value, String uniformName) {
        loadFloat(getUniformLocations().get(uniformName).get(0), value);
    }

    protected void bindAttribute(int attribute, String variableName) {
        glBindAttribLocation(programID, attribute, variableName);
    }

    protected int getUniformLocation(String uniformName) {
        return glGetUniformLocation(programID, uniformName);
    }

    protected void loadInt(int location, int value) {
        glUniform1i(location, value);
    }

    protected void loadFloat(int location, float value) {
        glUniform1f(location, value);
    }

    private void load3DVector(int location, Vector3f vector) {
        glUniform3f(location, vector.x, vector.y, vector.z);
    }

    private void load2DVector(int location, Vector2f vector) {
        glUniform2f(location, vector.x, vector.y);
    }


    private void loadMatrix(int location, Matrix4f matrix) {
        try (MemoryStack stack = stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            glUniformMatrix4fv(location, false, matrix.get(buffer));
        }
    }

    private int loadShader(String fileName, int type) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        String shaderSource;
        if (is != null) {
            shaderSource = new String(is.readAllBytes());
        } else {
            throw new IllegalArgumentException("Could not find resource from classpath: " + fileName);
        }
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, shaderSource);
        glCompileShader(shaderID);
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader could not be compiled" + glGetShaderInfoLog(shaderID));
        }
        return shaderID;
    }


    public void loadLights(List<Light> lights, Map<String, List<Integer>> uniformLocations) {
        List<Integer> lightPositions = uniformLocations.get("lightPosition");
        List<Integer> lightColours = uniformLocations.get("lightColour");
        List<Integer> attenuations = uniformLocations.get("attenuation");
        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (i < lights.size()) {
                load3DVector(lightPositions.get(i), lights.get(i).getPosition());
                load3DVector(lightColours.get(i), lights.get(i).getColour());
                load3DVector(attenuations.get(i), lights.get(i).getAttenuation());
            } else {
                load3DVector(lightPositions.get(i), new Vector3f());
                load3DVector(lightColours.get(i), new Vector3f());
                load3DVector(attenuations.get(i), new Vector3f(1, 0, 0));
            }
        }
    }
}
