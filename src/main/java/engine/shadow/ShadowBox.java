package engine.shadow;

import engine.DisplayManager;
import game.object.env.Camera;
import game.state.Game;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import util.math.MathUtil;

import static game.object.env.CameraUtil.FOV;
import static game.object.env.CameraUtil.NEAR_PLANE;

public class ShadowBox {

    private static float SHADOW_DISTANCE = 300;

    private float minX = Float.MAX_VALUE;
    private float maxX = Float.MIN_VALUE;
    private float minY = Float.MAX_VALUE;
    private float maxY = Float.MIN_VALUE;
    private float minZ = Float.MAX_VALUE;
    private float maxZ = Float.MIN_VALUE;

    private float farPlaneWidth, farPlaneHeight;
    private float nearPlaneWidth, nearPlaneHeight;

    private Matrix4f lightViewMatrix;

    public ShadowBox() {
        this.lightViewMatrix = new Matrix4f();
        calculatePlaneDimensions();
    }

    private void updateLightViewMatrix(Vector3f direction, Vector3f center) {
        Vector3f unitDirection = direction.normalize(new Vector3f());
        center.negate();
        lightViewMatrix.identity();
        float pitch = (float) Math.acos(new Vector2f(unitDirection.x, unitDirection.z).length());
        lightViewMatrix.rotate(pitch, new Vector3f(1, 0, 0));
        float yaw = (float) Math.toDegrees(((float) Math.atan(unitDirection.x / unitDirection.z)));
        yaw = unitDirection.z > 0 ? yaw - 180 : yaw;
        lightViewMatrix.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0));
        lightViewMatrix.translate(center);
    }


    public void update() {
        Vector4f[] frustumCorners = getViewFrustumCorners();
        reset();
        for (Vector4f frustumCorner : frustumCorners) {
            lightViewMatrix.transform(frustumCorner);
            if (frustumCorner.x < minX) {
                minX = frustumCorner.x;
            }
            if (frustumCorner.x > maxX) {
                maxX = frustumCorner.x;
            }
            if (frustumCorner.y < minY) {
                minY = frustumCorner.y;
            }
            if (frustumCorner.y > maxY) {
                maxY = frustumCorner.y;
            }
            if (frustumCorner.z < minZ) {
                minZ = frustumCorner.z;
            }
            if (frustumCorner.z > maxZ) {
                maxZ = frustumCorner.z;
            }
        }
        updateLightViewMatrix(Game.getInstance().getSun().getPosition().negate(new Vector3f()), getCenter());
    }

    private Vector4f[] getViewFrustumCorners() {
        Camera camera = Game.getInstance().getPlayerCamera();
        Vector4f[] viewCorners = new Vector4f[8];
        Vector3f playerPos = camera.getPlayer().getPosition();
        Vector3f cameraPos = camera.getPosition();
        Vector3f targetPos = new Vector3f(playerPos.x, playerPos.y + 20, playerPos.z);
        Vector3f cameraRay = targetPos.sub(cameraPos, new Vector3f()).normalize();
        Matrix4f cameraRotation = calculateCameraRotationMatrix();
        Vector3f upVector = MathUtil.getCoordinates(cameraRotation.transform(new Vector4f(0, 1, 0, 0)));
        Vector3f rightVector = upVector.cross(cameraRay, new Vector3f()).normalize();
        Vector3f toFarCenter = cameraPos.add(cameraRay.mul(SHADOW_DISTANCE, new Vector3f()), new Vector3f());
        Vector3f toNearCenter = cameraPos.add(cameraRay.mul(NEAR_PLANE, new Vector3f()), new Vector3f());
        //near plane corners
        viewCorners[0] = new Vector4f(toNearCenter.add(upVector.mul(nearPlaneHeight / 2, new Vector3f()), new Vector3f())
                .sub(rightVector.mul(nearPlaneWidth / 2, new Vector3f()), new Vector3f()), 1f);
        viewCorners[1] = new Vector4f(toNearCenter.add(upVector.mul(nearPlaneHeight / 2, new Vector3f()), new Vector3f())
                .add(rightVector.mul(nearPlaneWidth / 2, new Vector3f()), new Vector3f()), 1f);
        viewCorners[2] = new Vector4f(toNearCenter.sub(upVector.mul(nearPlaneHeight / 2, new Vector3f()), new Vector3f())
                .sub(rightVector.mul(nearPlaneWidth / 2, new Vector3f()), new Vector3f()), 1f);
        viewCorners[3] = new Vector4f(toNearCenter.sub(upVector.mul(nearPlaneHeight / 2, new Vector3f()), new Vector3f())
                .add(rightVector.mul(nearPlaneWidth / 2, new Vector3f()), new Vector3f()), 1f);
        //far plane corners
        viewCorners[4] = new Vector4f(toFarCenter.add(upVector.mul(farPlaneHeight / 2, new Vector3f()), new Vector3f())
                .sub(rightVector.mul(farPlaneWidth / 2, new Vector3f()), new Vector3f()), 1f);
        viewCorners[5] = new Vector4f(toFarCenter.add(upVector.mul(farPlaneHeight / 2, new Vector3f()), new Vector3f())
                .add(rightVector.mul(farPlaneWidth / 2, new Vector3f()), new Vector3f()), 1f);
        viewCorners[6] = new Vector4f(toFarCenter.sub(upVector.mul(farPlaneHeight / 2, new Vector3f()), new Vector3f())
                .sub(rightVector.mul(farPlaneWidth / 2, new Vector3f()), new Vector3f()), 1f);
        viewCorners[7] = new Vector4f(toFarCenter.sub(upVector.mul(farPlaneHeight / 2, new Vector3f()), new Vector3f())
                .add(rightVector.mul(farPlaneWidth / 2, new Vector3f()), new Vector3f()), 1f);
        return viewCorners;
    }

    private void calculatePlaneDimensions() {
        float aspectRatio = DisplayManager.getAspectRatio();
        nearPlaneHeight = ((float) (2 * Math.tan(Math.toRadians(FOV / 2)) * NEAR_PLANE));
        nearPlaneWidth = aspectRatio * nearPlaneHeight;
        farPlaneHeight = ((float) (2 * Math.tan(Math.toRadians(FOV / 2)) * SHADOW_DISTANCE));
        farPlaneWidth = aspectRatio * farPlaneHeight;
    }

    public Matrix4f getLightViewMatrix() {
        return lightViewMatrix;
    }

    public float getWidth() {
        return maxX - minX;
    }

    public float getHeight() {
        return maxY - minY;
    }

    public float getLength() {
        return maxZ - minZ;
    }

    public Vector3f getCenter() {
        float x = (minX + maxX) / 2f;
        float y = (minY + maxY) / 2f;
        float z = (minZ + maxZ) / 2f;
        Vector4f center = new Vector4f(x, y, z, 1);
        Matrix4f invertedLight = lightViewMatrix.invert(new Matrix4f());
        return MathUtil.getCoordinates(invertedLight.transform(center));
    }

    private void reset() {
        minX = Float.MAX_VALUE;
        maxX = Float.MIN_VALUE;
        minY = Float.MAX_VALUE;
        maxY = Float.MIN_VALUE;
        minZ = Float.MAX_VALUE;
        maxZ = Float.MIN_VALUE;
    }

    public Matrix4f calculateCameraRotationMatrix() {
        Camera camera = Game.getInstance().getPlayerCamera();
        Matrix4f rotation = new Matrix4f();
        rotation.rotate((float) Math.toRadians(-camera.getYaw()), new Vector3f(0, 1, 0));
        rotation.rotate((float) Math.toRadians(-camera.getPitch()), new Vector3f(1, 0, 0));
        return rotation;
    }
}
