package object.env;

import com.google.common.flogger.FluentLogger;
import engine.DisplayManager;
import game.state.GameState;
import game.state.State;
import interraction.MousePicker;
import object.Player;
import object.RenderObject;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import util.CameraUtil;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static final float MAX_PITCH = 70;
    private static final float MIN_PITCH = -70;
    private static final float PITCH_SPEED = 35;
    private static final float YAW_SPEED = 100;
    private static final float DISTANCE_FROM_PLAYER = 17;

    private Player player;
    private MousePicker mousePicker;
    private Vector3f position;
    private float pitch;

    public Camera(Player player) {
        this.player = player;
        this.position = new Vector3f();
        pitch = 10;
        if (glfwRawMouseMotionSupported()) {
            glfwSetInputMode(DisplayManager.getWindow(), GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
            logger.atInfo().log("Enabled raw mouse motion");
        }
    }

    public void checkState() {
        if (GameState.getInstance().getCurrentState().equals(State.IN_GAME)) {
            glfwSetInputMode(DisplayManager.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        } else {
            glfwSetInputMode(DisplayManager.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    }

    public void move(List<RenderObject> renderObjects) {
        Vector2f mouseCoords = mousePicker.calculateDeviceCoords();
        pitch += mouseCoords.y * PITCH_SPEED;
        pitch = Math.max(Math.min(pitch, MAX_PITCH), MIN_PITCH);
        player.getRotation().y += -mouseCoords.x * YAW_SPEED;
        GLFW.glfwSetCursorPos(DisplayManager.getWindow(), DisplayManager.getWidth() / 2f, DisplayManager.getHeight() / 2f);
        CameraUtil.calculateCameraPosition(this);
        CameraUtil.checkCameraCollision(this, renderObjects);
    }

    public Player getPlayer() {
        return player;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setMousePicker(MousePicker mousePicker) {
        this.mousePicker = mousePicker;
    }

    public Matrix4f createProjectionMatrix() {
        return new Matrix4f().perspective(((float) Math.toRadians(CameraUtil.FOV)),
                DisplayManager.getAspectRatio(), CameraUtil.NEAR_PLANE, CameraUtil.FAR_PLANE);
    }

    public float getDistanceFromPlayer() {
        return DISTANCE_FROM_PLAYER;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }
}
