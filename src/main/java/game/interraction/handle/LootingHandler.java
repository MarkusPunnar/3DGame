package game.interraction.handle;

import game.state.Game;
import game.state.HandlerState;
import game.state.State;
import game.interraction.LootableEntity;
import util.HandlerUtil;

import java.io.IOException;

public class LootingHandler implements Handler {

    @Override
    public void handle() throws IOException {
        if (Game.getInstance().getCurrentState() != State.IN_CHEST) {
            return;
        }
        LootableEntity currentLootable = HandlerState.getInstance().getLastLooted();
        if (currentLootable != null) {
            HandlerUtil.moveItems(currentLootable.getContent(), Game.getInstance().getPlayer().getInventory().getInventorySlots());
        }
    }
}
