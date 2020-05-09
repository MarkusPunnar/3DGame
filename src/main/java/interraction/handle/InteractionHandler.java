package interraction.handle;

import game.state.GameState;
import interraction.Interactable;
import object.Player;
import org.joml.Vector3f;
import engine.DisplayManager;

public class InteractionHandler implements Handler {

    private Player player;

    public InteractionHandler(Player player) {
        this.player = player;
    }

    public GameState handle(GameState state) {
        Vector3f playerPosition = player.getPosition();
        float closestDistance = Float.MAX_VALUE;
        for (Interactable object : state.getHandlerState().getInteractableObjects()) {
            object.setInteractionTime(object.getInteractionTime() + DisplayManager.getFrameTime());
            Vector3f objectPosition = object.getPosition();
            float distance = objectPosition.distance(playerPosition);
            if (distance < closestDistance) {
                state.getHandlerState().setClosestObject(object);
                closestDistance = distance;
            }
        }
        return state;
    }
}
