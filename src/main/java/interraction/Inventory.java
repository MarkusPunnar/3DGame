package interraction;

import engine.texture.GuiType;
import object.item.Slot;
import org.joml.Vector2f;

public class Inventory {

    private static final int INVENTORY_SIZE = 20;

    private Slot[] inventorySlots;
    private boolean isOpen;

    public Inventory() {
        this.inventorySlots = new Slot[INVENTORY_SIZE];
        for (int i = 0; i < inventorySlots.length; i++) {
            inventorySlots[i] = new Slot();
        }
    }

    public Slot[] getInventorySlots() {
        return inventorySlots;
    }

    public Slot initSlot(int textureID, int hoverID, Vector2f position, Vector2f scale, int index) {
        Slot slot = inventorySlots[index];
        slot.setID(textureID);
        slot.setNormalTextureID(textureID);
        slot.setHoverTextureID(hoverID);
        slot.setPosition(position);
        slot.setScale(scale);
        slot.setType(GuiType.SLOT);
        if (!slot.isFree()) {
            slot.getItem().getIcon().setPosition(position);
        }
        return slot;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
