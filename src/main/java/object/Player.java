package object;

import model.TexturedModel;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;
import renderEngine.DisplayManager;
import util.CollisionPacket;
import util.CollisionUtil;
import util.math.MathUtil;
import util.math.structure.*;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {

    private static final float RUN_SPEED = 30;
    private static final float TURN_SPEED = 160;
    private static final float JUMP_POWER = 80;
    private static final float UNITS_PER_METER = 50;

    public static final float PLAYER_HITBOX_X = 6f;
    public static final float PLAYER_HITBOX_Y = 9f;
    public static final float PLAYER_HITBOX_Z = 2.5f;

    private float currentSpeed;
    private float currentTurnSpeed;
    private float upwardsSpeed;
    private boolean isInAir;

    public Player(TexturedModel texturedModel, Vector3f position, float rotationX, float rotationY, float rotationZ, Vector3f scaleVector) {
        super(texturedModel, position, rotationX, rotationY, rotationZ, scaleVector);
        currentSpeed = 0;
        currentTurnSpeed = 0;
        isInAir = false;
    }

    public void move(List<Entity> loadedEntities) {
        checkInputs();
        increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTime(), 0);
        float distanceMoved = currentSpeed * DisplayManager.getFrameTime();
        float dx = (float) (Math.sin(Math.toRadians(getRotationY())) * distanceMoved);
        float dz = (float) (Math.cos(Math.toRadians(getRotationY())) * distanceMoved);
        Vector3f velocityR3 = new Vector3f(dx, upwardsSpeed * DisplayManager.getFrameTime(), dz);
        loadedEntities.remove(this);
        checkCollisionsAndSlide(loadedEntities, velocityR3);
        loadedEntities.add(this);
        currentSpeed = 0;
        currentTurnSpeed = 0;
        if (upwardsSpeed > 0) {
            upwardsSpeed--;
        } else {
            upwardsSpeed = 0;
        }
    }

    private void checkCollisionsAndSlide(List<Entity> loadedEntities, Vector3f velocityR3) {
        CollisionPacket playerPacket = new CollisionPacket(new Vector3f(PLAYER_HITBOX_X, PLAYER_HITBOX_Y, PLAYER_HITBOX_Z),
                velocityR3, new Vector3f(getPosition().x, getPosition().y + 9, getPosition().z));
        Vector3f playerPosition = collideWithWorld(playerPacket, loadedEntities, 0);
        Matrix3f ellipticInverse = MathUtil.getEllipticInverseMatrix();
        playerPacket.setBasePoint(playerPosition);
        Vector3f ellipticVelocity = new Vector3f(0, -0.08f, 0);
        playerPacket.setEllipticVelocity(ellipticVelocity);
        Vector3f normalizedVelocity = new Vector3f();
        playerPacket.setNormalizedEllipticVelocity(ellipticVelocity.normalize(normalizedVelocity));
        Vector3f finalPosition = collideWithWorld(playerPacket, loadedEntities, 0);
        finalPosition.mul(ellipticInverse);
        isInAir = !finalPosition.equals(playerPosition.mul(ellipticInverse));
        setPosition(new Vector3f(finalPosition.x, finalPosition.y - 9, finalPosition.z));
    }

    private Vector3f collideWithWorld(CollisionPacket packet, List<Entity> loadedEntities, int recursionDepth) {
        float unitScale = UNITS_PER_METER / 500.0f;
        float veryCloseDistance = 0.5f * unitScale;
        if (recursionDepth > 5) {
            return packet.getBasePoint();
        }
        packet = checkCollision(loadedEntities, packet);
        if (!packet.hasFoundCollision()) {
            Vector3f finalPos = new Vector3f();
            packet.getBasePoint().add(packet.getEllipticVelocity(), finalPos);
            return finalPos;
        }
        Vector3f newBasePoint = new Vector3f(packet.getBasePoint());
        Vector3f destinationPoint = new Vector3f();
        packet.getBasePoint().add(packet.getEllipticVelocity(), destinationPoint);
        if (packet.getNearestDistance() >= veryCloseDistance) {
            packet.getBasePoint().add(packet.getEllipticVelocity(), destinationPoint);
            Vector3f v = new Vector3f(packet.getEllipticVelocity());
            v.normalize(packet.getNearestDistance() - veryCloseDistance);
            packet.getBasePoint().add(v, newBasePoint);
            v.normalize();
            Vector3f scaledDistance = v.mul(veryCloseDistance);
            packet.getIntersectionPoint().sub(scaledDistance);
        }
        Vector3f slidePlaneOrigin = new Vector3f(packet.getIntersectionPoint());
        Vector3f slidePlaneNormal = new Vector3f();
        newBasePoint.sub(packet.getIntersectionPoint(), slidePlaneNormal);
        slidePlaneNormal.normalize();
        Plane3D slidingPlane = new Plane3D(slidePlaneNormal, slidePlaneOrigin);
        Vector3f newDestinationPoint = new Vector3f();
        float signedDistanceToDest = CollisionUtil.signedDistance(destinationPoint, slidingPlane);
        destinationPoint.sub(new Vector3f(slidePlaneNormal).mul(signedDistanceToDest), newDestinationPoint);
        Vector3f newVelocityVector = new Vector3f();
        newDestinationPoint.sub(packet.getIntersectionPoint(), newVelocityVector);
        if (newVelocityVector.length() < veryCloseDistance) {
            return newBasePoint;
        }
        packet.setEllipticVelocity(newVelocityVector);
        packet.setNormalizedEllipticVelocity(new Vector3f(newVelocityVector).normalize());
        packet.setBasePoint(newBasePoint);
        packet.setFoundCollision(false);
        return collideWithWorld(packet, loadedEntities, recursionDepth + 1);
    }

    private CollisionPacket checkCollision(List<Entity> loadedEntities, CollisionPacket packet) {
        Matrix3f ellipticMatrix = MathUtil.getEllipticMatrix();
        //Check every loaded entity except the player
        for (Entity entity : loadedEntities) {
            Matrix4f transformationMatrix = MathUtil.createTransformationMatrix(entity);
            List<Triangle> entityTriangles = entity.getTexturedModel().getRawModel().getTriangles();
            //Check every triangle
            for (Triangle triangle : entityTriangles) {
                //Convert coordinates to elliptic basis
                Vector3f[] verticesElliptic = CollisionUtil.convertToElliptic(ellipticMatrix, transformationMatrix, triangle);
                packet = CollisionUtil.checkTriangle(packet, verticesElliptic[0], verticesElliptic[1], verticesElliptic[2]);
            }
        }
        return packet;
    }

    private void checkInputs() {
        checkMovement();
        checkTurning();
        checkJump();
    }

    private void checkTurning() {
        long window = DisplayManager.getWindow();
        int aState = glfwGetKey(window, GLFW_KEY_A);
        int dState = glfwGetKey(window, GLFW_KEY_D);
        if (dState == GLFW_PRESS) {
            currentTurnSpeed = -TURN_SPEED;
        } else if (aState == GLFW_PRESS) {
            currentTurnSpeed = TURN_SPEED;
        }
    }

    private void checkJump() {
        long window = DisplayManager.getWindow();
        int spaceState = glfwGetKey(window, GLFW_KEY_SPACE);
        if (spaceState == GLFW_PRESS && !isInAir) {
            upwardsSpeed += JUMP_POWER;
        }
    }

    private void checkMovement() {
        long window = DisplayManager.getWindow();
        int wState = glfwGetKey(window, GLFW_KEY_W);
        int sState = glfwGetKey(window, GLFW_KEY_S);
        if (wState == GLFW_PRESS) {
            currentSpeed = RUN_SPEED;
        } else if (sState == GLFW_PRESS) {
            currentSpeed = -RUN_SPEED;
        }
    }
}
