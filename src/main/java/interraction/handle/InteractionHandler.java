package interraction.handle;

import game.state.GameState;
import game.state.HandlerState;
import interraction.Interactable;
import object.Player;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import engine.DisplayManager;

public class InteractionHandler implements Handler {

    private final float INTERACT_DISTANCE = 20f;

    private Player player;

    public InteractionHandler(Player player) {
        this.player = player;
    }

    public GameState handle(GameState state) {
        int fState = GLFW.glfwGetKey(DisplayManager.getWindow(), GLFW.GLFW_KEY_F);
        if (fState != GLFW.GLFW_PRESS) {
            return state;
        }
        Vector3f playerPosition = player.getPosition();
        Interactable closest = null;
        float closestDistance = Float.MAX_VALUE;
        for (Interactable object : state.getHandlerState().getInteractableObjects()) {
            Vector3f objectPosition = object.getPosition();
            float distance = objectPosition.distance(playerPosition);
            if (distance < closestDistance) {
                closest = object;
                closestDistance = distance;
            }
            object.setInteractionTime(object.getInteractionTime() + DisplayManager.getFrameTime());
        }

        if (closestDistance < INTERACT_DISTANCE) {
            closest.interact();
            state = closest.handleGui(state);
        }
        return state;
    }
}
