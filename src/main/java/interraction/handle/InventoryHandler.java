package interraction.handle;

import game.state.GameState;
import game.state.State;
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
    public GameState handle(GameState state) {
        if (state.getCurrentState() != State.IN_INVENTORY && state.getCurrentState() != State.IN_CHEST) {
            return state;
        }
        Slot[] inventorySlots = player.getInventory().getInventorySlots();
        Lootable openLootable = state.getHandlerState().getLastLooted();
        if (openLootable != null) {
            return HandlerUtil.moveItems(inventorySlots, openLootable.getContent(), state, mousePicker);
        } else {
            return state;
        }
    }
}
