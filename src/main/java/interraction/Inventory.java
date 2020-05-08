package interraction;

import object.item.Item;
import object.item.Slot;
import org.joml.Vector2f;

public class Inventory {

    private static final int INVENTORY_SIZE = 20;

    private Slot[] inventorySlots;

    public Inventory() {
        this.inventorySlots = new Slot[INVENTORY_SIZE];
        for (int i = 0; i < inventorySlots.length; i++) {
            inventorySlots[i] = new Slot();
        }
    }

    public Slot[] getInventorySlots() {
        return inventorySlots;
    }

    public void initSlot(int textureID, int hoverID, Vector2f position, Vector2f scale, int index) {
        Slot slot = inventorySlots[index];
        slot.setID(textureID);
        slot.setNormalTextureID(textureID);
        slot.setHoverTextureID(hoverID);
        slot.setPosition(position);
        slot.setScale(scale);
    }

    public boolean addItem(Item item) {
        for (Slot slot : inventorySlots) {
            if (slot != null && slot.isFree()) {
                slot.setItem(item);
                return true;
            }
        }
        return false;
    }
}
