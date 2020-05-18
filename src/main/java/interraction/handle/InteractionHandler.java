package interraction.handle;

import game.state.HandlerState;
import interraction.InteractableEntity;
import object.Player;
import org.joml.Vector3f;
import engine.DisplayManager;

public class InteractionHandler implements Handler {

    private Player player;

    public InteractionHandler(Player player) {
        this.player = player;
    }

    @Override
    public void handle() {
        Vector3f playerPosition = player.getPosition();
        float closestDistance = Float.MAX_VALUE;
        for (InteractableEntity object : HandlerState.getInstance().getInteractableEntities()) {
            object.setInteractionTime(object.getInteractionTime() + DisplayManager.getFrameTime());
            Vector3f objectPosition = object.getPosition();
            float distance = objectPosition.distance(playerPosition);
            if (distance < closestDistance) {
                HandlerState.getInstance().setClosestObject(object);
                closestDistance = distance;
            }
        }
    }
}
