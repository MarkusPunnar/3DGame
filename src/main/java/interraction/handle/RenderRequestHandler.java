package interraction.handle;

import engine.render.ParentRenderer;
import engine.render.RenderObject;
import engine.render.RenderRequest;
import engine.render.RequestInfo;
import game.state.GameState;
import game.state.State;
import engine.loader.Loader;
import engine.texture.GuiTexture;
import engine.texture.GuiType;
import interraction.Lootable;
import object.Player;
import object.item.Item;
import object.item.Slot;
import org.joml.Vector2f;
import util.math.MathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class RenderRequestHandler implements Handler {

    private final ParentRenderer renderer;
    private final Player player;

    public RenderRequestHandler(ParentRenderer renderer, Player player) {
        this.renderer = renderer;
        this.player = player;
    }

    public GameState handle(GameState state) {
        Queue<RenderRequest> requests = state.getHandlerState().getRequests();
        while (requests.peek() != null) {
            RenderRequest request = requests.poll();
            RequestInfo requestInfo = request.getRequestInfo();
            switch (request.getRequestType()) {
                case ADD:
                    switch (requestInfo.getGuiType()) {
                        case INVENTORY:
                            state.setCurrentState(State.IN_INVENTORY);
                            renderInventory(state, requestInfo);
                            player.getInventory().setOpen(true);
                            break;
                        case CHEST:
                            state.setCurrentState(State.IN_CHEST);
                            renderChestInterface(state, requestInfo);
                            break;
                        default:
                    }
                    break;
                case REMOVE:
                    switch (requestInfo.getGuiType()) {
                        case INVENTORY:
                            removeGui(List.of(GuiType.INVENTORY_TITLE, GuiType.SLOT, GuiType.SLOT_HOVER, GuiType.ICON));
                            state.setCurrentState(State.IN_GAME);
                            player.getInventory().setOpen(false);
                            break;
                        case CHEST:
                            removeGui(List.of(GuiType.CHEST_TITLE, GuiType.SLOT, GuiType.SLOT_HOVER, GuiType.ICON));
                            state.setCurrentState(State.IN_GAME);
                            state.getHandlerState().setLastLooted(null);
                            break;
                        default:
                    }
                    break;
                case REMOVE_ITEM:
                    renderer.getGuis().remove(requestInfo.getObject());
                default:
            }
        }
        return state;
    }

    private void removeGui(List<GuiType> typesToRemove) {
        for (RenderObject object : new ArrayList<>(renderer.getGuis())) {
            if (object instanceof GuiTexture) {
                GuiTexture gui = ((GuiTexture) object);
                if (typesToRemove.contains(gui.getType())) {
                    renderer.getGuis().remove(object);
                }
            }
        }
    }

    private void renderInventory(GameState state, RequestInfo requestInfo) {
        float n = 4;
        float m = 5;
        renderTitle(n, requestInfo, GuiType.INVENTORY_TITLE);
        renderGrid(n, m, requestInfo, state);
        renderItems(player.getInventory().getInventorySlots());
    }

    private void renderChestInterface(GameState state, RequestInfo requestInfo) {
        float n = 4;
        float m = 5;
        renderTitle(n, requestInfo, GuiType.CHEST_TITLE);
        renderGrid(n, m, requestInfo, state);
        renderChestItems(state);
    }

    private void renderChestItems(GameState state) {
        Lootable lastLootable = state.getHandlerState().getLastLooted();
        if (lastLootable == null) {
            return;
        }
        renderItems(lastLootable.getContent());

    }

    private void renderItems(Slot[] content) {
        for (Slot slot : content) {
            Item slotItem = slot.getItem();
            if (slotItem != null) {
                renderer.processGui(slotItem.getIcon());
            }
        }
    }

    private void renderTitle(float n, RequestInfo requestInfo, GuiType titleType) {
        Loader loader = renderer.getLoader();
        int titleTextureID = loader.loadTexture(titleType.getTextureName());
        Vector2f position = requestInfo.getTexturePosition();
        Vector2f scale = requestInfo.getTextureScale();
        float height = scale.y / n;
        float titleY = position.y + scale.y - height;
        Vector2f titlePosition = new Vector2f(position.x, titleY);
        Vector2f titleScale = new Vector2f(scale.x, height);
        renderer.processGui(new GuiTexture(titleTextureID, titlePosition, titleScale, GuiType.INVENTORY_TITLE, 0));
    }

    private void renderGrid(float n, float m, RequestInfo requestInfo, GameState state) {
        Vector2f scale = requestInfo.getTextureScale();
        Vector2f position = requestInfo.getTexturePosition();
        Loader loader = renderer.getLoader();
        int slotTextureID = loader.loadTexture(GuiType.SLOT.getTextureName());
        int slotHoverTextureID = loader.loadTexture(GuiType.SLOT_HOVER.getTextureName());
        float slotWidth = (2 * scale.x) / m;
        float slotHeight = (2 * scale.y) / n;
        Vector2f upperLeftCorner = new Vector2f(position.x - scale.x, position.y + scale.y - slotHeight);
        Vector2f slotScale = new Vector2f(slotWidth / 2, slotHeight / 2);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                Vector2f slotPosition = new Vector2f(MathUtil.roundFloat(upperLeftCorner.x + (j + 0.5f) * slotWidth, 4),
                        MathUtil.roundFloat(upperLeftCorner.y - (i + 0.5f) * slotHeight, 4));
                Slot slot;
                if (requestInfo.getGuiType() == GuiType.INVENTORY) {
                    slot = player.getInventory().initSlot(slotTextureID, slotHoverTextureID, slotPosition, slotScale, i * (int) m + j);
                }
                else {
                    Lootable currentLootable = state.getHandlerState().getLastLooted();
                    if (currentLootable == null) {
                        return;
                    }
                    slot = currentLootable.initSlot(slotTextureID, slotHoverTextureID, slotPosition, slotScale, i * (int) m + j);
                }
                renderer.processGui(slot);
            }
        }
    }
}
