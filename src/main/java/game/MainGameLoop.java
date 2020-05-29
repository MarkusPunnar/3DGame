package game;

import engine.DisplayManager;
import game.state.Game;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.net.URISyntaxException;

public class MainGameLoop {

    public static void main(String[] args) throws IOException, URISyntaxException {
        DisplayManager.createDisplay();
        Game gameInstance = Game.getInstance();
        gameInstance.init();
        while (!GLFW.glfwWindowShouldClose(DisplayManager.getWindow())) {
            gameInstance.update();
            DisplayManager.updateDisplay();
        }
        gameInstance.cleanUp();
        DisplayManager.closeDisplay();
    }
}
