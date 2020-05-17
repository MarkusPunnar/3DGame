package interraction.handle;

import game.state.GameState;
import interraction.LootableEntity;
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
    public void handle(GameState state) {
        switch (state.getCurrentState()) {
            case IN_INVENTORY:
                Slot[] inventorySlots = player.getInventory().getInventorySlots();
                HandlerUtil.moveItems(inventorySlots, inventorySlots, state, mousePicker);
            case IN_CHEST:
                inventorySlots = player.getInventory().getInventorySlots();
                LootableEntity openLootable = state.getHandlerState().getLastLooted();
                if (openLootable != null) {
                    HandlerUtil.moveItems(inventorySlots, openLootable.getContent(), state, mousePicker);
                }
            default:
        }
    }
}
