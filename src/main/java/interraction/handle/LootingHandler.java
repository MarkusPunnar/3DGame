package interraction.handle;

import game.state.GameState;
import game.state.HandlerState;
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
    public void handle() {
        if (GameState.getInstance().getCurrentState() != State.IN_CHEST) {
            return;
        }
        LootableEntity currentLootable = HandlerState.getInstance().getLastLooted();
        if (currentLootable != null) {
            HandlerUtil.moveItems(currentLootable.getContent(), player.getInventory().getInventorySlots(), mousePicker);
        }
    }
}
