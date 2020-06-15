package game.interraction;

import engine.model.TexturedModel;
import game.state.Game;
import game.object.Entity;
import org.joml.Vector3f;

public abstract class InteractableEntity extends Entity {

    protected float sinceLastInteraction;
    protected boolean isOpened;

    protected InteractableEntity(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scaleVector) {
        super(model, position, rotation, scaleVector);
        sinceLastInteraction = Float.MAX_VALUE;
        this.isOpened = false;
    }

    public abstract void interact();

    public abstract void handleGui(Game state);

    public float getInteractionTime() {
        return sinceLastInteraction;
    };

    public void setInteractionTime(float time) {
        this.sinceLastInteraction = time;
    }
}
