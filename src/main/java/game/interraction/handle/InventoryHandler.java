package game.interraction.handle;

import game.state.Game;
import game.state.HandlerState;
import game.interraction.LootableEntity;
import game.object.Player;
import game.object.item.Slot;
import game.ui.ObjectType;

import java.io.IOException;
import java.util.List;

public class InventoryHandler implements Handler {

    @Override
    public void handle() throws IOException {
        Player player = Game.getInstance().getPlayer();
        HandlerUtil.removeGui((List.of(ObjectType.SLOT)));
        switch (Game.getInstance().getCurrentState()) {
            case IN_INVENTORY:
                Slot[] inventorySlots = player.getInventory().getInventorySlots();
                HandlerUtil.moveItems(inventorySlots, inventorySlots);
                break;
            case IN_CHEST:
                inventorySlots = player.getInventory().getInventorySlots();
                LootableEntity openLootable = HandlerState.getInstance().getLastLooted();
                if (openLootable != null) {
                    HandlerUtil.moveItems(inventorySlots, openLootable.getContent());
                }
                break;
            default:
        }
    }
}
