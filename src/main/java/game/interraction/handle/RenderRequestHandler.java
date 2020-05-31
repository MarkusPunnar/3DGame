package game.interraction.handle;

import com.google.common.flogger.FluentLogger;
import engine.DisplayManager;
import engine.font.GUIText;
import engine.render.ParentRenderer;
import engine.render.RenderRequest;
import engine.render.RequestInfo;
import game.interraction.Inventory;
import game.interraction.LootableEntity;
import game.object.Player;
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
import util.GeneratorUtil;
import util.HandlerUtil;

import java.io.IOException;
import java.util.List;
import java.util.Queue;

public class RenderRequestHandler implements Handler {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final ParentRenderer renderer;
    private final Player player;

    public RenderRequestHandler() {
        this.renderer = Game.getInstance().getRenderer();
        this.player = Game.getInstance().getPlayer();
    }

    @Override
    public void handle() throws IOException {
        Queue<RenderRequest> requests = HandlerState.getInstance().getRequests();
        Game state = Game.getInstance();
        while (requests.peek() != null) {
            RenderRequest request = requests.poll();
            logger.atInfo().log("Resolving render request of type %s", request.toString());
            RequestInfo requestInfo = request.getRequestInfo();
            switch (request.getRequestType()) {
                case ADD:
                    switch (requestInfo.getGuiType()) {
                        case INVENTORY:
                            state.setCurrentState(State.IN_INVENTORY);
                            renderInventory(requestInfo);
                            player.getInventory().setOpen(true);
                            logger.atInfo().log("Rendered player inventory GUI");
                            break;
                        case CHEST:
                            state.setCurrentState(State.IN_CHEST);
                            renderChestInterface(requestInfo);
                            logger.atInfo().log("Rendered chest interface");
                            break;
                        default:
                    }
                    break;
                case REMOVE:
                    switch (requestInfo.getGuiType()) {
                        case INVENTORY:
                            HandlerUtil.removeGui(List.of(ObjectType.INVENTORY, ObjectType.SLOT));
                            renderer.getIcons().clear();
                            removeGuiTexts(player.getInventory().getInventorySlots());
                            state.setCurrentState(State.IN_GAME);
                            player.getInventory().setOpen(false);
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
                        default:
                    }
                    break;
                case REMOVE_ITEM:
                    renderer.getIcons().remove(requestInfo.getObject());
                    GUIText removeText = requestInfo.getGuiText();
                    if (removeText != null) {
                        renderer.removeText(removeText);
                    }
                    logger.atInfo().log("Removed item GUI of type %s", requestInfo.getObject().getClass().getSimpleName());
                    break;
                case REFRESH_TEXT:
                    renderer.loadText(requestInfo.getGuiText());
                    logger.atInfo().log("Refreshed GUI text");
                    break;
                case REMOVE_TEXT:
                    renderer.removeText(requestInfo.getGuiText());
                    logger.atInfo().log("Removed GUI text");
                    break;
                default:
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

    private void renderInventory(RequestInfo requestInfo) throws IOException {
        Inventory playerInventory = player.getInventory();
        playerInventory.updateSlots(requestInfo.getTexturePosition(), requestInfo.getTextureScale());
        renderer.getGuis().add(new UIComponent(GeneratorUtil.getTextureFromCache("inventory"), requestInfo.getTexturePosition(), requestInfo.getTextureScale(), ObjectType.INVENTORY));
        renderItems(playerInventory.getInventorySlots());
    }

    private void renderChestInterface(RequestInfo requestInfo) throws IOException {
        LootableEntity lastLootable = HandlerState.getInstance().getLastLooted();
        lastLootable.updateSlots(requestInfo.getTexturePosition(), requestInfo.getTextureScale());
        renderer.getGuis().add(new UIComponent(GeneratorUtil.getTextureFromCache("inventory"), requestInfo.getTexturePosition(), requestInfo.getTextureScale(), ObjectType.CHEST));
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
}
