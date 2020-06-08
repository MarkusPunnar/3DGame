package engine.render.request;

import com.google.common.flogger.FluentLogger;
import engine.DisplayManager;
import engine.font.GUIText;
import game.interraction.Inventory;
import game.interraction.LootableEntity;
import game.interraction.handle.HandlerUtil;
import game.object.generation.GeneratorUtil;
import game.object.item.Item;
import game.object.item.Slot;
import game.state.Game;
import game.state.HandlerState;
import game.state.State;
import game.ui.ObjectType;
import game.ui.UIComponent;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.List;

public class GuiRenderRequest extends RenderRequest {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final Vector2f position;
    private final Vector2f scale;
    private final String name;

    public GuiRenderRequest(RequestType requestType, ObjectType type) {
        super(requestType, type);
        this.position = null;
        this.scale = null;
        this.name = null;
    }

    public GuiRenderRequest(RequestType requestType, ObjectType type, Vector2f position, Vector2f scale) {
        this(requestType, type, position, scale, null);
    }

    public GuiRenderRequest(RequestType requestType, ObjectType type, Vector2f position, Vector2f scale, String name) {
        super(requestType, type);
        this.position = position;
        this.scale = scale;
        this.name = name;
    }

    @Override
    protected void handleUpdate() {
    }

    @Override
    protected void handleRemove() {
        Game state = Game.getInstance();
        switch (getObjectType()) {
            case INVENTORY:
                Inventory inventory = state.getPlayer().getInventory();
                HandlerUtil.removeGui(List.of(ObjectType.INVENTORY, ObjectType.SLOT));
                renderer.getIcons().clear();
                removeGuiTexts(inventory.getInventorySlots());
                state.setCurrentState(State.IN_GAME);
                inventory.setOpen(false);
                GLFW.glfwSetCursorPos(DisplayManager.getWindow(), DisplayManager.getWidth() / 2f, DisplayManager.getHeight() / 2f);
                logger.atInfo().log("Player inventory GUI removed");
                break;
            case CHEST:
                HandlerUtil.removeGui(List.of(ObjectType.CHEST, ObjectType.SLOT));
                renderer.getIcons().clear();
                removeGuiTexts(HandlerState.getInstance().getLastLooted().getContent());
                state.setCurrentState(State.IN_GAME);
                HandlerState.getInstance().setLastLooted(null);
                GLFW.glfwSetCursorPos(DisplayManager.getWindow(), DisplayManager.getWidth() / 2f, DisplayManager.getHeight() / 2f);
                logger.atInfo().log("Chest interface removed");
                break;

        }
    }

    @Override
    protected void handleAdd() throws IOException {
        Game state = Game.getInstance();
        switch (getObjectType()) {
            case INVENTORY:
                state.setCurrentState(State.IN_INVENTORY);
                renderInventory();
                state.getPlayer().getInventory().setOpen(true);
                logger.atInfo().log("Rendered player inventory GUI");
                break;
            case CHEST:
                state.setCurrentState(State.IN_CHEST);
                renderChestInterface();
                logger.atInfo().log("Rendered chest interface");
                break;
            case GUI:
                renderer.getGuis().add(new UIComponent(GeneratorUtil.getTextureFromCache(name),
                        position, scale, ObjectType.GUI));
                break;
            default:
        }
    }

    private void renderInventory() throws IOException {
        Inventory playerInventory = Game.getInstance().getPlayer().getInventory();
        playerInventory.updateSlots(position, scale);
        renderer.getGuis().add(new UIComponent(GeneratorUtil.getTextureFromCache("inventory"),
                position, scale, ObjectType.INVENTORY));
        renderItems(playerInventory.getInventorySlots());
    }

    private void renderChestInterface() throws IOException {
        LootableEntity lastLootable = HandlerState.getInstance().getLastLooted();
        lastLootable.updateSlots(position, scale);
        renderer.getGuis().add(new UIComponent(GeneratorUtil.getTextureFromCache("inventory"),
                position, scale, ObjectType.CHEST));
        renderChestItems(lastLootable);
    }

    private void renderChestItems(LootableEntity lastLootable) {
        if (lastLootable == null) {
            return;
        }
        renderItems(lastLootable.getContent());
    }

    private void renderItems(Slot[] content) {
        for (Slot slot : content) {
            Item slotItem = slot.getItem();
            if (slotItem != null) {
                GUIText itemText = slotItem.getGuiText();
                Vector3f iconPosition = slotItem.getIcon().getPosition();
                Vector2f textPosition = new Vector2f((1 + iconPosition.x) / 2f + slotItem.getIcon().getScaleVector().x * 0.15f,
                        Math.abs(iconPosition.y - 1) / 2f + slotItem.getIcon().getScaleVector().y * 0.2f);
                if (itemText == null) {
                    itemText = new GUIText.Builder(String.valueOf(slotItem.getAmount())).position(textPosition).fontSize(0.5f).colour(new Vector3f()).build();
                    slotItem.setGuiText(itemText);
                } else {
                    itemText.setPosition(textPosition);
                }
                renderer.processIcon(slotItem.getIcon());
                renderer.loadText(itemText);
            }
        }
    }

    private void removeGuiTexts(Slot[] slots) {
        for (Slot inventorySlot : slots) {
            if (!inventorySlot.isFree() && inventorySlot.getItem().getGuiText() != null) {
                renderer.removeText(inventorySlot.getItem().getGuiText());
            }
        }
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getScale() {
        return scale;
    }
}
