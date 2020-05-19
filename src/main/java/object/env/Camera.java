package object.env;

import game.state.GameState;
import game.state.State;
import interraction.MousePicker;
import object.Player;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import engine.DisplayManager;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Camera {

    private final float MAX_PITCH = 70;
    private final float MIN_PITCH = -70;
    private final float PITCHSPEED = 35;
    private final float YAWSPEED = 100;

    private float distanceFromPlayer = 17;

    private Player player;
    private MousePicker mousePicker;
    private Vector3f position;
    private float pitch;
    private float yaw;
    private float roll;

    public Camera(Player player) {
        this.player = player;
        this.position = new Vector3f();
        yaw = 0;
        pitch = 10;
        roll = 0;
        if (glfwRawMouseMotionSupported())
            glfwSetInputMode(DisplayManager.getWindow(), GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
    }

    public void checkState() {
        if (GameState.getInstance().getCurrentState().equals(State.IN_GAME)) {
            glfwSetInputMode(DisplayManager.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        } else {
            glfwSetInputMode(DisplayManager.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    }

    public void move() {
        Vector2f mouseCoords = mousePicker.calculateDeviceCoords();
        pitch += mouseCoords.y * PITCHSPEED;
        pitch = Math.max(Math.min(pitch, MAX_PITCH), MIN_PITCH);
        player.getRotation().y += -mouseCoords.x * YAWSPEED;
        GLFW.glfwSetCursorPos(DisplayManager.getWindow(), DisplayManager.getWidth() / 2f, DisplayManager.getHeight() / 2f);
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = ((float) Math.toRadians(player.getRotation().y));
        float offsetX = ((float) (Math.sin(theta) * horizontalDistance));
        float offsetZ = ((float) (Math.cos(theta) * horizontalDistance));
        position.x = player.getPosition().x - offsetX;
        position.y = player.getPosition().y + verticalDistance + 20;
        position.z = player.getPosition().z - offsetZ;
    }

    private float calculateHorizontalDistance() {
        return ((float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch))));
    }

    private float calculateVerticalDistance() {
        return ((float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch))));
    }

    public Player getPlayer() {
        return player;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void setMousePicker(MousePicker mousePicker) {
        this.mousePicker = mousePicker;
    }
}
