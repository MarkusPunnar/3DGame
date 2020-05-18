package interraction;

import engine.model.TexturedModel;
import game.state.GameState;
import object.Entity;
import org.joml.Vector3f;

public abstract class InteractableEntity extends Entity {

    protected float sinceLastInteraction;
    protected boolean isOpened;

    protected InteractableEntity(Builder builder) {
        super(builder);
        sinceLastInteraction = Float.MAX_VALUE;
        this.isOpened = builder.isOpened;
    }

    public abstract static class Builder extends Entity.Builder {

        private boolean isOpened = false;

        public Builder(TexturedModel texturedModel, Vector3f position) {
            super(texturedModel, position);
        }

        public Builder opened(boolean opened) {
            this.isOpened = opened;
            return self();
        }

        protected abstract Builder self();
    }

    public abstract void interact();

    public abstract void handleGui(GameState state);

    public float getInteractionTime() {
        return sinceLastInteraction;
    };

    public void setInteractionTime(float time) {
        this.sinceLastInteraction = time;
    }
}
