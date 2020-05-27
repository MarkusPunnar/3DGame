package interraction.handle;

import game.state.Game;
import game.state.HandlerState;
import interraction.LootableEntity;
import interraction.MousePicker;
import object.Player;
import object.item.Slot;
import util.HandlerUtil;

public class InventoryHandler implements Handler {

    @Override
    public void handle() {
        Player player = Game.getInstance().getPlayer();
        switch (Game.getInstance().getCurrentState()) {
            case IN_INVENTORY:
                Slot[] inventorySlots = player.getInventory().getInventorySlots();
                HandlerUtil.moveItems(inventorySlots, inventorySlots);
            case IN_CHEST:
                inventorySlots = player.getInventory().getInventorySlots();
                LootableEntity openLootable = HandlerState.getInstance().getLastLooted();
                if (openLootable != null) {
                    HandlerUtil.moveItems(inventorySlots, openLootable.getContent());
                }
            default:
        }
    }
}
