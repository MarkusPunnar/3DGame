package game.object;

import com.google.common.flogger.FluentLogger;
import engine.model.TexturedModel;
import engine.render.RenderRequest;
import engine.render.RequestInfo;
import engine.render.RequestType;
import game.ui.ObjectType;
import game.state.Game;
import game.state.HandlerState;
import game.interraction.InteractableEntity;
import game.interraction.Inventory;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import engine.DisplayManager;
import util.CollisionPacket;
import util.CollisionUtil;
import util.math.MathUtil;
import util.math.structure.*;
import util.octree.BoundingBox;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static final float FORWARD_SPEED = 35;
    private static final float SIDEWAYS_SPEED = 30;
    private static final float JUMP_POWER = 45;
    private static final float INTERACT_DISTANCE = 20f;

    private final Vector3f PLAYER_HITBOX = new Vector3f(4.5f, 9f, 4.5f);

    private float currentForwardSpeed;
    private float currentSidewaysSpeed;
    private float upwardsSpeed;
    private boolean isInAir;
    private boolean isMoving;
    private Inventory inventory;

    private Player(Builder builder) {
        super(builder);
        this.currentForwardSpeed = builder.currentForwardSpeed;
        this.currentSidewaysSpeed = builder.currentSidewaysSpeed;
        this.isInAir = builder.isInAir;
        this.inventory = builder.inventory;
    }

    public static class Builder extends Entity.Builder {

        private float currentForwardSpeed = 0;
        private float currentSidewaysSpeed = 0;
        private boolean isInAir = false;
        private Inventory inventory = new Inventory();

        public Builder(TexturedModel texturedModel, Vector3f position) {
            super(texturedModel, position);
        }

        public Builder scale(Vector3f scale) {
            super.scale(scale);
            return this;
        }

        public Player build() {
            return new Player(this);
        }
    }

    public void move(List<RenderObject> renderedObjects) {
        isMoving = false;
        checkInputs();
        float distanceMovedForward = currentForwardSpeed * DisplayManager.getFrameTime();
        float dx = (float) (Math.sin(Math.toRadians(getRotation().y)) * distanceMovedForward);
        float dz = (float) (Math.cos(Math.toRadians(getRotation().y)) * distanceMovedForward);
        float distanceMovedSideways = currentSidewaysSpeed * DisplayManager.getFrameTime();
        dx -= (float) (Math.sin(Math.toRadians(getRotation().y - 90))) * distanceMovedSideways;
        dz -= (float) (Math.cos(Math.toRadians(getRotation().y - 90))) * distanceMovedSideways;
        Vector3f velocityR3 = new Vector3f(dx, upwardsSpeed * DisplayManager.getFrameTime() * 2f, dz);
        renderedObjects.remove(this);
        checkCollisionsAndSlide(renderedObjects, velocityR3);
        renderedObjects.add(this);
        currentForwardSpeed = 0;
        currentSidewaysSpeed = 0;
        if (upwardsSpeed > 0) {
            upwardsSpeed -= 75f * DisplayManager.getFrameTime();
        } else {
            upwardsSpeed = 0;
        }
    }

    private void checkCollisionsAndSlide(List<RenderObject> renderedObjects, Vector3f velocityR3) {
        CollisionPacket playerPacket = new CollisionPacket(velocityR3, new Vector3f(getPosition().x, getPosition().y + PLAYER_HITBOX.y - 0.1f, getPosition().z), PLAYER_HITBOX);
        Vector3f playerPosition = velocityR3.equals(new Vector3f(), 0.001f) ? playerPacket.getBasePoint() : collideWithWorld(playerPacket, renderedObjects,  0);
        Matrix3f ellipticInverse = MathUtil.getEllipticInverseMatrix(PLAYER_HITBOX);
        playerPacket.setBasePoint(playerPosition);
        Vector3f ellipticVelocity = new Vector3f(0, -0.08f, 0);
        playerPacket.setMovementVelocity(new Vector3f(playerPacket.getEllipticVelocity()));
        playerPacket.setEllipticVelocity(ellipticVelocity);
        Vector3f normalizedVelocity = new Vector3f();
        playerPacket.setNormalizedEllipticVelocity(ellipticVelocity.normalize(normalizedVelocity));
        Vector3f finalPosition = collideWithWorld(playerPacket, renderedObjects, 0);
        finalPosition.mul(ellipticInverse);
        isInAir = !finalPosition.equals(playerPosition.mul(ellipticInverse), 0.01f);
        setPosition(new Vector3f(finalPosition.x, finalPosition.y - PLAYER_HITBOX.y + 0.1f, finalPosition.z));
    }

    private Vector3f collideWithWorld(CollisionPacket packet, List<RenderObject> renderedObjects, int recursionDepth) {
        float veryCloseDistance = (float) Math.pow(10, -3);
        if (recursionDepth > 5) {
            return packet.getBasePoint();
        }
        BoundingBox playerCheckBox = new BoundingBox(new Vector3f(getPosition().x - PLAYER_HITBOX.x - 2,
                getPosition().y - PLAYER_HITBOX.y - 2, getPosition().z - PLAYER_HITBOX.z - 2),
                new Vector3f(getPosition().x + PLAYER_HITBOX.x + 2, getPosition().y + PLAYER_HITBOX.y + 2, getPosition().z + PLAYER_HITBOX.z + 2));
        CollisionUtil.checkCollision(renderedObjects, packet, playerCheckBox);
        if (!packet.hasFoundCollision()) {
            return packet.getBasePoint().add(packet.getEllipticVelocity(), new Vector3f());
        }
        Vector3f newBasePoint = new Vector3f(packet.getBasePoint());
        Vector3f destinationPoint = packet.getBasePoint().add(packet.getEllipticVelocity(), new Vector3f());;
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
        if (packet.getNearestDistance() == 0.0f) {
            Vector3f newVelocityVector = slidePlaneNormal.normalize(0.012f);
            if (packet.getBasePoint().y < packet.getIntersectionPoint().y) {
                newVelocityVector.mul(8);
                upwardsSpeed = 0;
            }
            newBasePoint.add(newVelocityVector);
            return newBasePoint;
        }
        slidePlaneNormal.normalize();
        Plane3D slidingPlane = new Plane3D(slidePlaneNormal, slidePlaneOrigin);
        float signedDistanceToDest = CollisionUtil.signedDistance(destinationPoint, slidingPlane);
        Vector3f newDestinationPoint = destinationPoint.sub(new Vector3f(slidePlaneNormal).mul(signedDistanceToDest, new Vector3f()), new Vector3f());;
        Vector3f newVelocityVector = newDestinationPoint.sub(packet.getIntersectionPoint(),  new Vector3f());
        if (newVelocityVector.length() < veryCloseDistance) {
            return newBasePoint;
        }
        packet.setEllipticVelocity(newVelocityVector);
        packet.setNormalizedEllipticVelocity(new Vector3f(newVelocityVector).normalize());
        packet.setBasePoint(newBasePoint);
        packet.setFoundCollision(false);
        return collideWithWorld(packet, renderedObjects, recursionDepth + 1);
    }

    private void checkInputs() {
        checkForwardMovement();
        checkSidewaysMovement();
        checkJump();
    }

    private void checkSidewaysMovement() {
        long window = DisplayManager.getWindow();
        int aState = glfwGetKey(window, GLFW_KEY_A);
        int dState = glfwGetKey(window, GLFW_KEY_D);
        if (aState == GLFW_PRESS) {
            currentSidewaysSpeed = SIDEWAYS_SPEED;
            isMoving = true;
        } else if (dState == GLFW_PRESS) {
            currentSidewaysSpeed = -SIDEWAYS_SPEED;
            isMoving = true;
        }
    }

    private void checkJump() {
        long window = DisplayManager.getWindow();
        int spaceState = glfwGetKey(window, GLFW_KEY_SPACE);
        if (spaceState == GLFW_PRESS && !isInAir) {
            upwardsSpeed = JUMP_POWER;
            isMoving = true;
            logger.atInfo().log("Registered player jump");
        }
    }

    private void checkForwardMovement() {
        long window = DisplayManager.getWindow();
        int wState = glfwGetKey(window, GLFW_KEY_W);
        int sState = glfwGetKey(window, GLFW_KEY_S);
        if (wState == GLFW_PRESS) {
            currentForwardSpeed = FORWARD_SPEED;
            isMoving = true;
        } else if (sState == GLFW_PRESS) {
            currentForwardSpeed = -FORWARD_SPEED;
            isMoving = true;
        }
    }

    public boolean isMoving() {
        return isMoving;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void interactWithInventory() {
        if (!inventory.isOpen()) {
            logger.atInfo().log("Request to open inventory sent");
            HandlerState.getInstance().registerRequest(new RenderRequest(RequestType.ADD, new RequestInfo(new Vector2f(0, -0.15f), new Vector2f(1.6f, 1.6f), ObjectType.INVENTORY)));
        }
        else {
            logger.atInfo().log("Request to close inventory sent");
            HandlerState.getInstance().registerRequest(new RenderRequest(RequestType.REMOVE, new RequestInfo(ObjectType.INVENTORY)));
        }
    }

    public void interactWithObject() {
        Game state = Game.getInstance();
        InteractableEntity closestObject = HandlerState.getInstance().getClosestObject();
        float closestDistance = getPosition().distance(closestObject.getPosition());
        if (closestDistance < INTERACT_DISTANCE) {
            logger.atInfo().log("Found close interactable game.object of type %s", closestObject.getClass().getSimpleName());
            closestObject.interact();
            state.getCurrentTree().update(closestObject);
            closestObject.handleGui(state);
        }
    }
}
