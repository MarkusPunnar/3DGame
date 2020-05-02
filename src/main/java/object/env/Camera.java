package object.env;

import object.Player;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import renderEngine.DisplayManager;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Camera {

    private final float MAX_PITCH = 40;
    private final float MIN_PITCH = 0;

    private float distanceFromPlayer = 30;

    private boolean rightMouseButtonPressed;

    private Player player;
    private Vector3f position;
    private Vector2f cursorPos;
    private float pitch;
    private float yaw;
    private float roll;

    public Camera(Player player) {
        this.player = player;
        this.position = new Vector3f();
        this.cursorPos = new Vector2f();
        yaw = 0;
        pitch = 0;
        roll = 0;
        rightMouseButtonPressed = false;
        setCallBacks();
    }

    public void move() {
        if (rightMouseButtonPressed) {
           try (MemoryStack stack = stackPush()) {
               DoubleBuffer xBuffer = stack.callocDouble(1);
               DoubleBuffer yBuffer = stack.callocDouble(1);
               glfwGetCursorPos(DisplayManager.getWindow(), xBuffer, yBuffer);
               float x = (float) xBuffer.get();
               float y = (float) yBuffer.get();
               if (!cursorPos.equals(new Vector2f())) {
                   float pitchChange = pitch + (cursorPos.y - y);
                   if (pitchChange > MAX_PITCH) {
                       pitch = MAX_PITCH;
                   } else {
                       pitch = Math.max(pitchChange, MIN_PITCH);
                   }
               }
               cursorPos.x = x;
               cursorPos.y = y;
           }
        }
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = ((float) Math.toRadians(player.getRotationY()));
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

    private void setCallBacks() {
        long window = DisplayManager.getWindow();
        glfwSetScrollCallback(window, (current, x, y) -> distanceFromPlayer -= y);
        glfwSetMouseButtonCallback(window, (current, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                rightMouseButtonPressed = action == GLFW_PRESS;
            }
        });
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
}
