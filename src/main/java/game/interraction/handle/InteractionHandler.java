package game.interraction.handle;

import game.state.Game;
import game.state.HandlerState;
import game.interraction.InteractableEntity;
import org.joml.Vector3f;
import engine.DisplayManager;

public class InteractionHandler implements Handler {

    @Override
    public void handle() {
        Vector3f playerPosition = Game.getInstance().getPlayer().getPosition();
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
