package game.object.env;

import engine.DisplayManager;
import game.object.Player;
import game.object.RenderObject;
import game.state.Game;
import org.joml.Vector3f;
import engine.collision.CollisionPacket;
import engine.collision.CollisionUtil;
import util.octree.BoundingBox;

import java.util.List;

public class CameraUtil {

    public static final float FOV = 55;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 1000;

    private static final float MIN_CAMERA_DISTANCE = 0.1f;
    private static final float MAX_FRAME_OFFSET = 4f;
    private static float CACHE_STEP_SIZE = 2f;

    public static void calculateCameraPosition(List<RenderObject> renderObjects) {
        Camera camera = Game.getInstance().getPlayerCamera();
        Player player = camera.getPlayer();
        float theta = ((float) Math.toRadians(player.getRotation().y));
        float horizontalDistance = calculateHorizontalDistance(camera);
        float verticalDistance = calculateVerticalDistance(camera);
        float offsetX = ((float) (Math.sin(theta) * horizontalDistance));
        float offsetZ = ((float) (Math.cos(theta) * horizontalDistance));
        Vector3f tempPosition = new Vector3f(player.getPosition().x - offsetX, player.getPosition().y + verticalDistance + 20, player.getPosition().z - offsetZ);
        checkCameraCollision(tempPosition, renderObjects);
    }

    private static float calculateHorizontalDistance(Camera camera) {
        return ((float) (camera.getDistanceFromPlayer() * Math.cos(Math.toRadians(camera.getPitch()))));
    }

    private static float calculateVerticalDistance(Camera camera) {
        return ((float) (camera.getDistanceFromPlayer() * Math.sin(Math.toRadians(camera.getPitch()))));
    }

    private static void checkCameraCollision(Vector3f tempPosition, List<RenderObject> renderObjects) {
        Camera camera = Game.getInstance().getPlayerCamera();
        Vector3f playerPos = camera.getPlayer().getPosition();
        CACHE_STEP_SIZE = camera.getPlayer().isMoving() ? 3f : 1f;
        Vector3f targetPos = new Vector3f(playerPos.x, playerPos.y + 20, playerPos.z);
        float nearPlaneHeight = ((float) (2 * Math.tan(Math.toRadians(FOV / 2)) * NEAR_PLANE));
        float nearPlaneWidth = DisplayManager.getAspectRatio() * nearPlaneHeight;
        float rootTwo = (float) Math.sqrt(2);
        CollisionPacket cameraPacket = new CollisionPacket(tempPosition.sub(targetPos, new Vector3f()), targetPos,
                new Vector3f(rootTwo * nearPlaneWidth, rootTwo * nearPlaneHeight, 1));
        float distanceFromPlayer = camera.getDistanceFromPlayer();
        Vector3f newPosition = null;
        if (camera.hasMovementCached()) {
            newPosition = camera.getPosition().add(camera.getMovementCache(), new Vector3f());
        }
        else {
            BoundingBox checkBox = new BoundingBox(new Vector3f(playerPos.x - distanceFromPlayer, playerPos.y - distanceFromPlayer, playerPos.z - distanceFromPlayer),
                    new Vector3f(playerPos.x + distanceFromPlayer, playerPos.y + distanceFromPlayer, playerPos.z + distanceFromPlayer));
            CollisionUtil.checkCollision(renderObjects, cameraPacket, checkBox);
        }
        if (cameraPacket.hasFoundCollision() || newPosition != null) {
            float collisionTime = cameraPacket.getNearestDistance() / cameraPacket.getEllipticVelocity().length();
            if (collisionTime == 0.0f) {
                collisionTime = MIN_CAMERA_DISTANCE;
            }
            Vector3f toCameraVector = cameraPacket.getVelocityR3().mul(collisionTime, new Vector3f());
            if (newPosition == null) {
                newPosition = targetPos.add(toCameraVector);
            }
            moveGradually(camera, newPosition, camera.getPosition());
            camera.setStateZoom();
        } else {
            if (camera.isZoomed()) {
                moveGradually(camera, tempPosition, camera.getPosition());
                camera.setStateNormal();
            } else {
                camera.setPosition(tempPosition);
            }
        }
    }

    private static void moveGradually(Camera camera, Vector3f newPosition, Vector3f oldPosition) {
        Vector3f changeVector = oldPosition.sub(newPosition, new Vector3f());
        float offset = changeVector.length();
        if (offset > MAX_FRAME_OFFSET) {
            offset -= CACHE_STEP_SIZE;
            Vector3f offsetVector = changeVector.normalize(offset);
            Vector3f adjustedPos = newPosition.add(offsetVector, new Vector3f());
            camera.setMovementCache(newPosition.sub(adjustedPos, new Vector3f()));
            newPosition = adjustedPos;

        } else {
            camera.setMovementCache(null);
        }
        camera.setPosition(newPosition);
    }
}
