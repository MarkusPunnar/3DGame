package game.interraction;

import engine.model.TexturedModel;
import game.object.item.Icon;
import game.object.item.Slot;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class LootableEntity extends InteractableEntity {

    private static final float SLOT_BEGIN_X = -0.288f;
    private static final float SLOT_BEGIN_Y = 0.18f;
    private static final float SLOT_STEP_X = 0.125f;
    private static final float SLOT_STEP_Y = -0.223f;
    private static final float SLOT_SCALE_X = 0.048f;
    private static final float SLOT_SCALE_Y = 0.079f;


    protected Slot[] content;
    private boolean isInitialized;

    protected LootableEntity(Builder builder) {
        super(builder);
        this.content = new Slot[builder.capacity];
        for (int i = 0; i < content.length; i++) {
            content[i] = new Slot();
        }
    }

    public void updateSlots(Vector2f middlePosition, Vector2f scale) {
        for (int i = 0; i < content.length; i++) {
            Slot slot = content[i];
            Vector2f position = new Vector2f(middlePosition.x + (SLOT_BEGIN_X + (i % 6) * SLOT_STEP_X) * scale.x,
                    middlePosition.y + (SLOT_BEGIN_Y + Math.floorDiv(i, 6) * SLOT_STEP_Y) * scale.y);
            slot.setPosition(position);
            slot.setScale(new Vector2f(SLOT_SCALE_X * scale.x, SLOT_SCALE_Y * scale.y));
            if (!slot.isFree()) {
                Icon slotItemIcon = slot.getItem().getIcon();
                slotItemIcon.setPosition(position);
                slotItemIcon.setScale(new Vector2f(slot.getScale().x / 1.3f, slot.getScale().y / 1.3f));
            }
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
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }
}
