package object;

import engine.model.TexturedModel;
import engine.render.RenderRequest;
import engine.render.RequestInfo;
import engine.render.RequestType;
import engine.texture.ObjectType;
import game.state.GameState;
import game.state.HandlerState;
import interraction.InteractableEntity;
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

    private static final float FORWARD_SPEED = 35;
    private static final float SIDEWAYS_SPEED = 30;
    private static final float JUMP_POWER = 45;
    private static final float UNITS_PER_METER = 50;
    private static final float INTERACT_DISTANCE = 20f;

    public static final float PLAYER_HITBOX_X = 4.5f;
    public static final float PLAYER_HITBOX_Y = 9f;
    public static final float PLAYER_HITBOX_Z = 4.5f;

    private float currentForwardSpeed;
    private float currentSidewaysSpeed;
    private float upwardsSpeed;
    private boolean isInAir;
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
        CollisionPacket playerPacket = new CollisionPacket(velocityR3, new Vector3f(getPosition().x, getPosition().y + PLAYER_HITBOX_Y, getPosition().z));
        Vector3f playerPosition = velocityR3.equals(new Vector3f()) ? playerPacket.getBasePoint() : collideWithWorld(playerPacket, renderedObjects,  0);
        Matrix3f ellipticInverse = MathUtil.getEllipticInverseMatrix();
        playerPacket.setBasePoint(playerPosition);
        Vector3f ellipticVelocity = new Vector3f(0, -0.08f, 0);
        playerPacket.setEllipticVelocity(ellipticVelocity);
        Vector3f normalizedVelocity = new Vector3f();
        playerPacket.setNormalizedEllipticVelocity(ellipticVelocity.normalize(normalizedVelocity));
        Vector3f finalPosition = collideWithWorld(playerPacket, renderedObjects, 0);
        finalPosition.mul(ellipticInverse);
        isInAir = !finalPosition.equals(playerPosition.mul(ellipticInverse), 0.01f);
        setPosition(new Vector3f(finalPosition.x, finalPosition.y - PLAYER_HITBOX_Y, finalPosition.z));
    }

    private Vector3f collideWithWorld(CollisionPacket packet, List<RenderObject> renderedObjects, int recursionDepth) {
        float unitScale = UNITS_PER_METER / 500.0f;
        float veryCloseDistance = 0.5f * unitScale;
        if (recursionDepth > 5) {
            return packet.getBasePoint();
        }
        checkCollision(renderedObjects, packet);
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
        if (packet.getNearestDistance() == 0.0f && packet.hasFoundCollision()) {
            newBasePoint.add(slidePlaneNormal.normalize(0.01f));
            return newBasePoint;
        }
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
        return collideWithWorld(packet, renderedObjects, recursionDepth + 1);
    }

    private void checkCollision(List<RenderObject> renderedObjects, CollisionPacket packet) {
        Matrix3f ellipticMatrix = MathUtil.getEllipticMatrix();
        BoundingBox playerBox = new BoundingBox(new Vector3f(getPosition().x - PLAYER_HITBOX_X - 2, getPosition().y - PLAYER_HITBOX_Y - 2, getPosition().z - PLAYER_HITBOX_Z - 2),
                new Vector3f(getPosition().x + PLAYER_HITBOX_X + 2, getPosition().y + PLAYER_HITBOX_Y + 2, getPosition().z + PLAYER_HITBOX_Z + 2));
        Set<Triangle> closeTriangles = GameState.getInstance().getCurrentTree().getCloseTriangles(playerBox);
        for (RenderObject object : renderedObjects) {
            Matrix4f transformationMatrix = MathUtil.createTransformationMatrix(object);
            List<Triangle> objectTriangles = object.getModel().getRawModel().getTriangles();
            for (Triangle triangle : objectTriangles) {
                Triangle worldTriangle = new Triangle(CollisionUtil.convertToWorld(transformationMatrix, triangle));
                if (closeTriangles.contains(worldTriangle)) {
                    Vector3f[] verticesElliptic = CollisionUtil.convertToElliptic(ellipticMatrix, worldTriangle);
                    CollisionUtil.checkTriangle(packet, verticesElliptic[0], verticesElliptic[1], verticesElliptic[2]);
                }
            }
        }
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
            upwardsSpeed = JUMP_POWER;
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

    public void interactWithInventory() {
        if (!inventory.isOpen()) {
            HandlerState.getInstance().registerRequest(new RenderRequest(RequestType.ADD, new RequestInfo(new Vector2f(0, 0.15f), new Vector2f(0.6f, 0.6f), ObjectType.INVENTORY)));
        }
        else {
            HandlerState.getInstance().registerRequest(new RenderRequest(RequestType.REMOVE, new RequestInfo(ObjectType.INVENTORY)));
        }
    }

    public void interactWithObject() {
        GameState state = GameState.getInstance();
        InteractableEntity closestObject = HandlerState.getInstance().getClosestObject();
        float closestDistance = getPosition().distance(closestObject.getPosition());
        if (closestDistance < INTERACT_DISTANCE) {
            closestObject.interact();
            state.getCurrentTree().update(closestObject);
            closestObject.handleGui(state);
        }
    }
}
