package interraction.handle;

import engine.DisplayManager;
import game.state.GameState;
import interraction.Lootable;
import interraction.MousePicker;
import object.Player;
import object.item.Slot;
import org.lwjgl.glfw.GLFW;
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
                Lootable openLootable = state.getHandlerState().getLastLooted();
                if (openLootable != null) {
                    HandlerUtil.moveItems(inventorySlots, openLootable.getContent(), state, mousePicker);
                }
            default:
        }
    }
}
