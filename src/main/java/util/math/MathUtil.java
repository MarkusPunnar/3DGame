package util.math;

import game.object.RenderObject;
import game.object.env.Camera;
import game.object.env.Light;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MathUtil {


    public static Matrix4f createTransformationMatrix(RenderObject object) {
        return createTransformationMatrix(object.getPosition(), object.getRotation(), object.getScaleVector());
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scaleVector) {
        Matrix4f matrix = new Matrix4f();
        return matrix.translate(translation)
                .rotate(((float) Math.toRadians(rotation.x)), new Vector3f(0, 0, 1))
                .rotate(((float) Math.toRadians(rotation.y)), new Vector3f(0, 1, 0))
                .rotate(((float) Math.toRadians(rotation.z)), new Vector3f(1, 0, 0))
                .scale(scaleVector);
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f matrix = new Matrix4f();
        Vector3f cameraPos = camera.getPosition();
        Vector3f playerPos = camera.getPlayer().getPosition();
        return matrix.lookAt(cameraPos, new Vector3f(playerPos.x, playerPos.y + 20, playerPos.z), new Vector3f(0, 1, 0));
    }

    public static Matrix3f getEllipticMatrix(Vector3f hitbox) {
        Matrix3f matrix = new Matrix3f();
        matrix.m00 = 1 / hitbox.x;
        matrix.m11 = 1 / hitbox.y;
        matrix.m22 = 1 / hitbox.z;
        return matrix;
    }

    public static Matrix3f getEllipticInverseMatrix(Vector3f hitbox) {
        Matrix3f matrix = new Matrix3f();
        matrix.m00 = hitbox.x;
        matrix.m11 = hitbox.y;
        matrix.m22 = hitbox.z;
        return matrix;
    }

    public static Matrix4f getLightSpaceMatrix(Light sun, Camera camera) {
        Matrix4f orthoProjectionMatrix = new Matrix4f().orthoSymmetric(500, 500, 1, 15000);
        Matrix4f lightViewMatrix = new Matrix4f().lookAt(sun.getPosition(),camera.getPosition(), new Vector3f(0, 1, 0));
        return orthoProjectionMatrix.mul(lightViewMatrix, new Matrix4f());
    }

    public static Vector3f getCoordinates(Vector4f vector) {
        return new Vector3f(vector.x, vector.y, vector.z);
    }

    public static boolean floatEquals(float a, float b) {
        return Math.abs(a - b) <= Math.pow(10, -2);
    }

    public static float solveQuadratic(float a, float b, float c, float maxVal) {
        float determinant = b * b - 4 * a * c;
        if (determinant < 0) {
            return Float.MAX_VALUE;
        }
        float x1 = (float) ((-b + Math.sqrt(determinant)) / (2 * a));
        float x2 = (float) ((-b - Math.sqrt(determinant)) / (2 * a));
        if (x2 < x1) {
            float temp = x2;
            x2 = x1;
            x1 = temp;
        }
        if (x1 > 0 && x1 < maxVal) {
            return x1;
        }
        if (x2 > 0 && x2 < maxVal) {
            return x2;
        }
        return Float.MAX_VALUE;
    }
}