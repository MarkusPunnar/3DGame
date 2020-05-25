package engine;

import com.google.common.flogger.FluentLogger;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class DisplayManager {

    private static int width = 1280;
    private static int height = 720;
    private static long window = NULL;

    private static double lastFrameTime = 0.0;
    private static float delta;

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public static void createDisplay() {
        //Set the error callback routine to use System.err
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) { //Init GLFW First
            throw new RuntimeException("ERROR: GLFW not initialized");
        }
        logger.atInfo().log("Initialized GLFW");
        glfwDefaultWindowHints();
        window = glfwCreateWindow(width, height, "First display", NULL, NULL); //Create the window
        if (window == NULL) {
            throw new RuntimeException("ERROR: Window not created");
        }
        logger.atInfo().log("Created the main window");
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); //Get integer buffers
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight); //Read window size into buffers
            GLFWVidMode vidMode = glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            if (vidMode != null) { //Set screen to center
                glfwSetWindowPos(window, (vidMode.width() - pWidth.get()) / 2, (vidMode.height() - pHeight.get()) / 2);
            }
        }
        lastFrameTime = GLFW.glfwGetTime();
        delta = ((float) lastFrameTime);
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); //Enable V-Sync
        GL.createCapabilities();
        glfwShowWindow(window); //Show screen
        logger.atInfo().log("Display created successfully");
    }

    public static void updateDisplay() {
        glfwPollEvents();
        glfwSwapBuffers(window);
        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            glfwGetWindowSize(window, widthBuffer, heightBuffer);
            width = widthBuffer.get();
            height = heightBuffer.get();
        }
        double currentFrameTime = GLFW.glfwGetTime();
        delta = ((float) (currentFrameTime - lastFrameTime));
        lastFrameTime = currentFrameTime;
    }

    public static void closeDisplay() {
        Callbacks.glfwFreeCallbacks(window); //Release callbacks
        glfwDestroyWindow(window); //Destroy the window
        glfwTerminate(); //Terminate GLFW
        logger.atInfo().log("Terminated GLFW");
        glfwSetErrorCallback(null).free();
    }

    public static long getWindow() {
        return window;
    }

    public static float getFrameTime() {
        return delta;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static float getAspectRatio() {
        return (float) width / (float) height;
    }
}
