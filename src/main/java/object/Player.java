package object;

import engine.loader.Loader;
import engine.model.TexturedModel;
import engine.render.RenderObject;
import engine.render.RenderRequest;
import engine.render.RequestInfo;
import engine.render.RequestType;
import engine.texture.GuiType;
import game.state.GameState;
import interraction.Interactable;
import interraction.Inventory;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import engine.DisplayManager;
import util.CollisionPacket;
import util.CollisionUtil;
import util.math.MathUtil;
import util.math.structure.*;
import util.octree.BoundingBox;

import java.util.List;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {

    private static final float FORWARD_SPEED = 30;
    private static final float SIDEWAYS_SPEED = 30;
    private static final float JUMP_POWER = 40;
    private static final float UNITS_PER_METER = 50;
    private static final float INTERACT_DISTANCE = 20f;

    public static final float PLAYER_HITBOX_X = 6f;
    public static final float PLAYER_HITBOX_Y = 9f;
    public static final float PLAYER_HITBOX_Z = 2.5f;

    private float currentForwardSpeed;
    private float currentSidewaysSpeed;
    private float upwardsSpeed;
    private boolean isInAir;
    private Inventory inventory;

    public Player(TexturedModel texturedModel, Vector3f position, Vector3f rotation, Vector3f scaleVector) {
        super(texturedModel, position, rotation, scaleVector);
        currentForwardSpeed = 0;
        currentSidewaysSpeed = 0;
        isInAir = false;
        inventory = new Inventory();
    }

    public void move(List<RenderObject> renderedObjects, GameState state) {
        checkInputs();
        float distanceMovedForward = currentForwardSpeed * DisplayManager.getFrameTime();
        float dx = (float) (Math.sin(Math.toRadians(getRotation().y)) * distanceMovedForward);
        float dz = (float) (Math.cos(Math.toRadians(getRotation().y)) * distanceMovedForward);
        float distanceMovedSideways = currentSidewaysSpeed * DisplayManager.getFrameTime();
        dx -= (float) (Math.sin(Math.toRadians(getRotation().y - 90))) * distanceMovedSideways;
        dz -= (float) (Math.cos(Math.toRadians(getRotation().y - 90))) * distanceMovedSideways;
        Vector3f velocityR3 = new Vector3f(dx, upwardsSpeed * DisplayManager.getFrameTime(), dz);
        renderedObjects.remove(this);
        checkCollisionsAndSlide(renderedObjects, state, velocityR3);
        renderedObjects.add(this);
        currentForwardSpeed = 0;
        currentSidewaysSpeed = 0;
        if (upwardsSpeed > 0) {
            upwardsSpeed--;
        } else {
            upwardsSpeed = 0;
        }
    }

    private void checkCollisionsAndSlide(List<RenderObject> renderedObjects, GameState state, Vector3f velocityR3) {
        CollisionPacket playerPacket = new CollisionPacket(velocityR3, new Vector3f(getPosition().x, getPosition().y + PLAYER_HITBOX_Y, getPosition().z));
        Vector3f playerPosition = velocityR3.equals(new Vector3f()) ? playerPacket.getBasePoint() : collideWithWorld(playerPacket, renderedObjects, state,  0);
        Matrix3f ellipticInverse = MathUtil.getEllipticInverseMatrix();
        playerPacket.setBasePoint(playerPosition);
        Vector3f ellipticVelocity = new Vector3f(0, -0.08f, 0);
        playerPacket.setEllipticVelocity(ellipticVelocity);
        Vector3f normalizedVelocity = new Vector3f();
        playerPacket.setNormalizedEllipticVelocity(ellipticVelocity.normalize(normalizedVelocity));
        Vector3f finalPosition = collideWithWorld(playerPacket, renderedObjects, state, 0);
        finalPosition.mul(ellipticInverse);
        isInAir = !finalPosition.equals(playerPosition.mul(ellipticInverse), 0.01f);
        setPosition(new Vector3f(finalPosition.x, finalPosition.y - 9, finalPosition.z));
    }

    private Vector3f collideWithWorld(CollisionPacket packet, List<RenderObject> renderedObjects, GameState state, int recursionDepth) {
        float unitScale = UNITS_PER_METER / 500.0f;
        float veryCloseDistance = 0.5f * unitScale;
        if (recursionDepth > 5) {
            return packet.getBasePoint();
        }
        packet = checkCollision(renderedObjects, state, packet);
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
        return collideWithWorld(packet, renderedObjects, state, recursionDepth + 1);
    }

    private CollisionPacket checkCollision(List<RenderObject> renderedObjects, GameState state, CollisionPacket packet) {
        Matrix3f ellipticMatrix = MathUtil.getEllipticMatrix();
        BoundingBox playerBox = new BoundingBox(new Vector3f(getPosition().x - PLAYER_HITBOX_X - 2, getPosition().y - PLAYER_HITBOX_Y - 2, getPosition().z - PLAYER_HITBOX_Z - 2),
                new Vector3f(getPosition().x + PLAYER_HITBOX_X + 2, getPosition().y + PLAYER_HITBOX_Y + 2, getPosition().z + PLAYER_HITBOX_Z + 2));
        Set<Triangle> closeTriangles = state.getCurrentTree().getCloseTriangles(playerBox);
        for (RenderObject object : renderedObjects) {
            Matrix4f transformationMatrix = MathUtil.createTransformationMatrix(object);
            List<Triangle> objectTriangles = object.getTexturedModel().getRawModel().getTriangles();
            for (Triangle triangle : objectTriangles) {
                Triangle worldTriangle = new Triangle(CollisionUtil.convertToWorld(transformationMatrix, triangle));
                if (closeTriangles.contains(worldTriangle)) {
                    Vector3f[] verticesElliptic = CollisionUtil.convertToElliptic(ellipticMatrix, worldTriangle);
                    packet = CollisionUtil.checkTriangle(packet, verticesElliptic[0], verticesElliptic[1], verticesElliptic[2]);
                }
            }
        }
        return packet;
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
        } else if (dState == GLFW_PRESS) {
            currentSidewaysSpeed = -SIDEWAYS_SPEED;
        }
    }

    private void checkJump() {
        long window = DisplayManager.getWindow();
        int spaceState = glfwGetKey(window, GLFW_KEY_SPACE);
        if (spaceState == GLFW_PRESS && !isInAir) {
            upwardsSpeed += JUMP_POWER;
        }
    }

    private void checkForwardMovement() {
        long window = DisplayManager.getWindow();
        int wState = glfwGetKey(window, GLFW_KEY_W);
        int sState = glfwGetKey(window, GLFW_KEY_S);
        if (wState == GLFW_PRESS) {
            currentForwardSpeed = FORWARD_SPEED;
        } else if (sState == GLFW_PRESS) {
            currentForwardSpeed = -FORWARD_SPEED;
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void interactWithInventory(GameState state) {
        if (!inventory.isOpen()) {
            state.getHandlerState().registerRequest(new RenderRequest(RequestType.ADD, new RequestInfo(new Vector2f(0, 0.15f), new Vector2f(0.6f, 0.6f), GuiType.INVENTORY)));
        }
        else {
            state.getHandlerState().registerRequest(new RenderRequest(RequestType.REMOVE, new RequestInfo(GuiType.INVENTORY)));
        }
    }

    public GameState interactWithObject(GameState state) {
        Interactable closestObject = state.getHandlerState().getClosestObject();
        float closestDistance = getPosition().distance(closestObject.getPosition());
        if (closestDistance < INTERACT_DISTANCE) {
            closestObject.interact();
            state = closestObject.handleGui(state);
        }
        return state;
    }
}
