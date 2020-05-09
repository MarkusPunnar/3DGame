package interraction.handle;

import game.state.GameState;
import game.state.State;
import interraction.Lootable;
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
    public GameState handle(GameState state) {
        if (state.getCurrentState() != State.IN_CHEST) {
            return state;
        }
        Lootable currentLootable = state.getHandlerState().getLastLooted();
        if (currentLootable == null) {
            return state;
        }
        GameState gameState = HandlerUtil.moveItems(currentLootable.getContent(), player.getInventory().getInventorySlots(), state, mousePicker);
        return gameState;
    }
}
