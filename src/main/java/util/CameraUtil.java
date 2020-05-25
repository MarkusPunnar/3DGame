package util;

import engine.DisplayManager;
import object.Player;
import object.RenderObject;
import object.env.Camera;
import org.joml.Vector3f;
import util.octree.BoundingBox;

import java.util.List;

public class CameraUtil {

    public static final float FOV = 70;
    public static final float NEAR_PLANE = 1f;
    public static final float FAR_PLANE = 500;

    private static final float MIN_CAMERA_DISTANCE = 0.1f;

    public static void calculateCameraPosition(Camera camera) {
        Vector3f position = camera.getPosition();
        Player player = camera.getPlayer();
        float theta = ((float) Math.toRadians(player.getRotation().y));
        float horizontalDistance = calculateHorizontalDistance(camera);
        float verticalDistance = calculateVerticalDistance(camera);
        float offsetX = ((float) (Math.sin(theta) * horizontalDistance));
        float offsetZ = ((float) (Math.cos(theta) * horizontalDistance));
        position.x = player.getPosition().x - offsetX;
        position.y = player.getPosition().y + verticalDistance + 20;
        position.z = player.getPosition().z - offsetZ;
    }

    private static float calculateHorizontalDistance(Camera camera) {
        return ((float) (camera.getDistanceFromPlayer() * Math.cos(Math.toRadians(camera.getPitch()))));
    }

    private static float calculateVerticalDistance(Camera camera) {
        return ((float) (camera.getDistanceFromPlayer() * Math.sin(Math.toRadians(camera.getPitch()))));
    }

    public static void checkCameraCollision(Camera camera, List<RenderObject> renderObjects) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f playerPos = camera.getPlayer().getPosition();
        Vector3f targetPos = new Vector3f(playerPos.x, playerPos.y + 20, playerPos.z);
        float nearPlaneHeight = ((float) (2 * Math.tan(Math.toRadians(FOV / 2)) * NEAR_PLANE));
        float nearPlaneWidth = DisplayManager.getAspectRatio() * nearPlaneHeight;
        float rootTwo = (float) Math.sqrt(2);
        CollisionPacket cameraPacket = new CollisionPacket(cameraPos.sub(targetPos, new Vector3f()), targetPos,
                new Vector3f(rootTwo * nearPlaneWidth, rootTwo * nearPlaneHeight, 0.2f));
        float distanceFromPlayer = camera.getDistanceFromPlayer();
        BoundingBox checkBox = new BoundingBox(new Vector3f(playerPos.x - distanceFromPlayer, playerPos.y - distanceFromPlayer, playerPos.z - distanceFromPlayer),
                new Vector3f(playerPos.x + distanceFromPlayer, playerPos.y + distanceFromPlayer, playerPos.z + distanceFromPlayer));
        CollisionUtil.checkCollision(renderObjects, cameraPacket, checkBox);
        if (cameraPacket.hasFoundCollision()) {
            float collisionTime = cameraPacket.getNearestDistance() / cameraPacket.getEllipticVelocity().length();
            if (collisionTime == 0.0f) {
                collisionTime = MIN_CAMERA_DISTANCE;
            }
            Vector3f toCameraVector = cameraPacket.getVelocityR3().mul(collisionTime, new Vector3f());
            camera.setPosition(targetPos.add(toCameraVector));
        }
    }
}
