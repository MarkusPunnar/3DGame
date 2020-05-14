package interraction.handle;

import engine.DisplayManager;
import game.state.GameState;
import game.state.State;
import interraction.Lootable;
import interraction.MousePicker;
import object.Player;
import org.lwjgl.glfw.GLFW;
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
        Lootable currentLootable = state.getHandlerState().getLastLooted();
        if (currentLootable != null) {
            HandlerUtil.moveItems(currentLootable.getContent(), player.getInventory().getInventorySlots(), state, mousePicker);
        }
    }
}
