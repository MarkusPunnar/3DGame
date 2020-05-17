package interraction.handle;

import game.state.GameState;
import game.state.State;
import interraction.LootableEntity;
import interraction.MousePicker;
import object.Player;
import util.HandlerUtil;

public class LootingHandler implements Handler {

    private Player player;
    private MousePicker mousePicker;

    public LootingHandler(Player player, MousePicker mousePicker) {
        this.player = player;
        this.mousePicker = mousePicker;
    }

    @Override
    public void handle(GameState state) {
        if (state.getCurrentState() != State.IN_CHEST) {
            return;
        }
        LootableEntity currentLootable = state.getHandlerState().getLastLooted();
        if (currentLootable != null) {
            HandlerUtil.moveItems(currentLootable.getContent(), player.getInventory().getInventorySlots(), state, mousePicker);
        }
    }
}
