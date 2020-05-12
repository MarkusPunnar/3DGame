package engine.shader;

import object.env.Light;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface Shader {

    void start();

    void stop();

    void cleanUp();

    void loadLight(Light light);

    void doLoadMatrix(Matrix4f matrix, String uniformName);

}
