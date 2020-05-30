package game.interraction;

import engine.model.TexturedModel;
import game.object.item.Slot;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class LootableEntity extends InteractableEntity {

    protected Slot[] content;

    protected LootableEntity(Builder builder) {
        super(builder);
        this.content = new Slot[builder.capacity];
        for (int i = 0; i < content.length; i++) {
            content[i] = new Slot();
        }
    }

    public abstract static class Builder extends InteractableEntity.Builder {

        private int capacity = 5;

        public Builder(TexturedModel texturedModel, Vector3f position) {
            super(texturedModel, position);
        }

        public Builder capacity(int capacity) {
            this.capacity = capacity;
            return self();
        }

        protected abstract Builder self();
    }

    public Slot[] getContent() {
        return content;
    };

    public abstract Slot initSlot(int textureID, int hoverID, Vector2f position, Vector2f scale, int index);
}
