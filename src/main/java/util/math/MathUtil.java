package util.math;

import entity.Entity;
import entity.Player;
import entity.env.Camera;
import org.joml.*;

import java.lang.Math;

public class MathUtil {

    public static Matrix4f createTransformationMatrix(Entity entity) {
        return createTransformationMatrix(entity.getPosition(), entity.getRotationX(), entity.getRotationY(), entity.getRotationZ(), entity.getScaleVector());
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, Vector3f scaleVector) {
        Matrix4f matrix = new Matrix4f();
        return matrix.translate(translation)
                .rotate(((float) Math.toRadians(rz)), new Vector3f(0, 0, 1))
                .rotate(((float) Math.toRadians(ry)), new Vector3f(0, 1, 0))
                .rotate(((float) Math.toRadians(rx)), new Vector3f(1, 0, 0))
                .scale(scaleVector);
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f matrix = new Matrix4f();
        Vector3f cameraPos = camera.getPosition();
        Vector3f playerPos = camera.getPlayer().getPosition();
        return matrix.lookAt(cameraPos, new Vector3f(playerPos.x, playerPos.y + 10, playerPos.z), new Vector3f(0, 1, 0));
    }

    public static Matrix3f getEllipticMatrix() {
        Matrix3f matrix = new Matrix3f();
        matrix.m00 = 1 / Player.PLAYER_HITBOX_X;
        matrix.m11 = 1 / Player.PLAYER_HITBOX_Y;
        matrix.m22 = 1 / Player.PLAYER_HITBOX_Z;
        return matrix;
    }

    public static Matrix3f getEllipticInverseMatrix() {
        Matrix3f matrix = new Matrix3f();
        matrix.m00 = Player.PLAYER_HITBOX_X;
        matrix.m11 = Player.PLAYER_HITBOX_Y;
        matrix.m22 = Player.PLAYER_HITBOX_Z;
        return matrix;
    }

    public static Vector3f getCoordinates(Vector4f vector) {
        return new Vector3f(vector.x, vector.y, vector.z);
    }

    public static boolean floatEquals(float a, float b) {
        return Math.abs(a - b) <= Math.pow(10, -4);
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