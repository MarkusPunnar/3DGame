package game.interraction;

import game.object.item.Icon;
import game.object.item.Slot;
import org.joml.Vector2f;

public class Inventory {

    private static final float SLOT_BEGIN_X = -0.288f;
    private static final float SLOT_BEGIN_Y = 0.18f;
    private static final float SLOT_STEP_X = 0.125f;
    private static final float SLOT_STEP_Y = -0.223f;
    private static final float SLOT_SCALE_X = 0.048f;
    private static final float SLOT_SCALE_Y = 0.079f;

    private static final int INVENTORY_SIZE = 18;

    private Slot[] inventorySlots;
    private boolean isOpen;
    private boolean isInitialized;

    public Inventory() {
        this.inventorySlots = new Slot[INVENTORY_SIZE];
        for (int i = 0; i < inventorySlots.length; i++) {
            inventorySlots[i] = new Slot();
        }
    }

    public Slot[] getInventorySlots() {
        return inventorySlots;
    }


    public void updateSlots(Vector2f middlePosition, Vector2f scale) {
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            Slot slot = inventorySlots[i];
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

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }
}
