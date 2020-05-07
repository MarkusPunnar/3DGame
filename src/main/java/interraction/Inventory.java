package interraction;

import object.item.Item;
import object.item.Slot;

public class Inventory {

    private static final int INVENTORY_SIZE = 20;

    private Slot[] inventory;

    public Inventory() {
        this.inventory = new Slot[INVENTORY_SIZE];
    }

    public Slot[] getInventory() {
        return inventory;
    }

    public void registerSlot(Slot slot, int index) {
        inventory[index] = slot;
    }

    public boolean addItem(Item item) {
        for (Slot slot : inventory) {
            if (slot != null && slot.isFree()) {
                slot.setItem(item);
                return true;
            }
        }
        return false;
    }
}
