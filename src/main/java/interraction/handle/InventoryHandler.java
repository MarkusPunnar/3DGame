package interraction.handle;

import game.state.GameState;
import game.state.HandlerState;
import interraction.Lootable;
import interraction.MousePicker;
import object.Player;
import object.item.Slot;
import util.HandlerUtil;

public class InventoryHandler implements Handler {

    private Player player;
    private MousePicker mousePicker;

    public InventoryHandler(Player player, MousePicker mousePicker) {
        this.player = player;
        this.mousePicker = mousePicker;
    }

    @Override
    public void handle() {
        switch (GameState.getInstance().getCurrentState()) {
            case IN_INVENTORY:
                Slot[] inventorySlots = player.getInventory().getInventorySlots();
                HandlerUtil.moveItems(inventorySlots, inventorySlots, mousePicker);
            case IN_CHEST:
                inventorySlots = player.getInventory().getInventorySlots();
                Lootable openLootable = HandlerState.getInstance().getLastLooted();
                if (openLootable != null) {
                    HandlerUtil.moveItems(inventorySlots, openLootable.getContent(), mousePicker);
                }
            default:
        }
    }
}
