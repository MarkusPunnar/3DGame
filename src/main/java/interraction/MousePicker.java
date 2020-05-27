package interraction;

import game.state.Game;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import engine.DisplayManager;
import util.math.MathUtil;

import java.nio.DoubleBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;

public class MousePicker {

    private Vector3f currentRay;
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;

    public MousePicker() {
        this.projectionMatrix = Game.getInstance().getRenderer().getProjectionMatrix();
        this.viewMatrix = MathUtil.createViewMatrix(Game.getInstance().getPlayerCamera());
    }

    public void update() {
        viewMatrix = MathUtil.createViewMatrix(Game.getInstance().getPlayerCamera());
        currentRay = calculateMouseRay();
    }

    private Vector3f calculateMouseRay() {
        Vector2f normalizedDeviceCoords = calculateDeviceCoords();
        Vector4f clipCoords = new Vector4f(normalizedDeviceCoords.x, normalizedDeviceCoords.y, -1f, 1f);
        return convertToWorldSpace(convertToViewSpace(clipCoords));
    }

    public Vector2f calculateDeviceCoords() {
        float mouseX;
        float mouseY;
        try (MemoryStack stack = stackPush()) {
            DoubleBuffer xBuf = stack.mallocDouble(1);
            DoubleBuffer yBuf = stack.mallocDouble(1);
            GLFW.glfwGetCursorPos(DisplayManager.getWindow(), xBuf, yBuf);
            mouseX = ((float) xBuf.get());
            mouseY = ((float) yBuf.get());
        }
        return getNormalizedDeviceCoords(mouseX, mouseY);
    }

    private Vector4f convertToViewSpace(Vector4f clipCoords) {
        Matrix4f invertedProjection = new Matrix4f();
        projectionMatrix.invert(invertedProjection);
        Vector4f viewCoords = new Vector4f();
        invertedProjection.transform(clipCoords, viewCoords);
        return new Vector4f(viewCoords.x, viewCoords.y, -1f, 0f);
    }

    private Vector3f convertToWorldSpace(Vector4f viewCoords) {
        Matrix4f invertedView = new Matrix4f();
        viewMatrix.invert(invertedView);
        Vector4f worldCoords = new Vector4f();
        invertedView.transform(viewCoords, worldCoords);
        Vector3f worldRay = new Vector3f(worldCoords.x, worldCoords.y, worldCoords.z);
        worldRay.normalize();
        return worldRay;
    }

    private Vector2f getNormalizedDeviceCoords(float mouseX, float mouseY) {
        float x = (2 * mouseX) / DisplayManager.getWidth() - 1;
        float y = (2 * mouseY) / DisplayManager.getHeight() - 1;
        return new Vector2f(x, -y);
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }
}
