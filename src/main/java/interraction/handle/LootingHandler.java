package interraction.handle;

import game.state.Game;
import game.state.HandlerState;
import game.state.State;
import interraction.LootableEntity;
import interraction.MousePicker;
import object.Player;
import util.HandlerUtil;

public class LootingHandler implements Handler {

    @Override
    public void handle() {
        if (Game.getInstance().getCurrentState() != State.IN_CHEST) {
            return;
        }
        LootableEntity currentLootable = HandlerState.getInstance().getLastLooted();
        if (currentLootable != null) {
            HandlerUtil.moveItems(currentLootable.getContent(), Game.getInstance().getPlayer().getInventory().getInventorySlots());
        }
    }
}
