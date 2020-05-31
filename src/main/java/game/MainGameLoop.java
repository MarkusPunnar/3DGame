package game;

import com.google.common.flogger.FluentLogger;
import engine.DisplayManager;
import game.state.Game;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class MainGameLoop {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private static int fpsCount = 0;
    private static float sinceLastLog = 0;

    public static void main(String[] args) throws IOException {
        DisplayManager.createDisplay();
        Game gameInstance = Game.getInstance();
        gameInstance.init();
        while (!GLFW.glfwWindowShouldClose(DisplayManager.getWindow())) {
            if (sinceLastLog > 5) {
                logger.atInfo().log("Current FPS: %d", fpsCount / 5);
                sinceLastLog = 0;
                fpsCount = 0;
            }
            gameInstance.update();
            DisplayManager.updateDisplay();
            fpsCount++;
            sinceLastLog += DisplayManager.getFrameTime();
        }
        gameInstance.cleanUp();
        DisplayManager.closeDisplay();
    }
}
