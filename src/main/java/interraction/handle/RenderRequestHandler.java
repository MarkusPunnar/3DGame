package interraction.handle;

import com.google.common.flogger.FluentLogger;
import engine.DisplayManager;
import engine.font.GUIText;
import engine.font.structure.FontType;
import engine.render.ParentRenderer;
import game.state.HandlerState;
import interraction.LootableEntity;
import object.RenderObject;
import engine.render.RenderRequest;
import engine.render.RequestInfo;
import game.state.GameState;
import game.state.State;
import engine.loader.VAOLoader;
import engine.texture.GuiTexture;
import engine.texture.ObjectType;
import object.Player;
import object.item.Item;
import object.item.Slot;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import util.math.MathUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class RenderRequestHandler implements Handler {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final ParentRenderer renderer;
    private FontType font;
    private final Player player;

    public RenderRequestHandler(ParentRenderer renderer, Player player, String fontName) throws IOException, URISyntaxException {
        this.renderer = renderer;
        this.font = new FontType(renderer.getLoader().loadFontAtlas(fontName));
        this.player = player;
    }

    @Override
    public void handle() {
        Queue<RenderRequest> requests = HandlerState.getInstance().getRequests();
        GameState state = GameState.getInstance();
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
                            removeGui(List.of(ObjectType.INVENTORY_TITLE, ObjectType.SLOT, ObjectType.SLOT_HOVER, ObjectType.ICON));
                            state.setCurrentState(State.IN_GAME);
                            player.getInventory().setOpen(false);
                            GLFW.glfwSetCursorPos(DisplayManager.getWindow(), DisplayManager.getWidth() / 2f, DisplayManager.getHeight() / 2f);
                            logger.atInfo().log("Player inventory GUI removed");
                            break;
                        case CHEST:
                            removeGui(List.of(ObjectType.CHEST_TITLE, ObjectType.SLOT, ObjectType.SLOT_HOVER, ObjectType.ICON));
                            state.setCurrentState(State.IN_GAME);
                            HandlerState.getInstance().setLastLooted(null);
                            GLFW.glfwSetCursorPos(DisplayManager.getWindow(), DisplayManager.getWidth() / 2f, DisplayManager.getHeight() / 2f);
                            logger.atInfo().log("Chest interface removed");
                            break;
                        default:
                    }
                    break;
                case REMOVE_ITEM:
                    renderer.getGuis().remove(requestInfo.getObject());
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

    private void removeGui(List<ObjectType> typesToRemove) {
        for (RenderObject object : new ArrayList<>(renderer.getGuis())) {
                if (typesToRemove.contains(object.getType())) {
                    renderer.getGuis().remove(object);
                    if (object.getGuiText() != null) {
                        renderer.removeText(object.getGuiText());
                    }
                }
        }
    }

    private void renderInventory(RequestInfo requestInfo) {
        float n = 4;
        float m = 5;
        renderTitle(n, requestInfo, ObjectType.INVENTORY_TITLE);
        renderGrid(n, m, requestInfo);
        renderItems(player.getInventory().getInventorySlots());
    }

    private void renderChestInterface(RequestInfo requestInfo) {
        float n = 4;
        float m = 5;
        renderTitle(n, requestInfo, ObjectType.CHEST_TITLE);
        renderGrid(n, m, requestInfo);
        renderChestItems();
    }

    private void renderChestItems() {
        LootableEntity lastLootable = HandlerState.getInstance().getLastLooted();
        if (lastLootable == null) {
            return;
        }
        renderItems(lastLootable.getContent());

    }

    private void renderItems(Slot[] content) {
        for (Slot slot : content) {
            Item slotItem = slot.getItem();
            if (slotItem != null) {
                GUIText itemText = slot.getGuiText();
                Vector3f iconPosition = slotItem.getIcon().getPosition();
                if (itemText == null) {
                    Vector2f textPosition = new Vector2f((1 + iconPosition.x) / 2f + slotItem.getPaddingX(), Math.abs(iconPosition.y - 1) / 2f + slotItem.getPaddingY());
                    itemText = new GUIText.Builder(String.valueOf(slotItem.getAmount()), font).position(textPosition).fontSize(0.6f).build();
                    itemText.setColour(1, 1, 1);
                    slot.setGuiText(itemText);
                } else {
                    itemText.setPosition(new Vector2f((1 + iconPosition.x) / 2f + slotItem.getPaddingX(), Math.abs(iconPosition.y - 1) / 2f + slotItem.getPaddingY()));
                }
                renderer.processGui(slotItem.getIcon());
                renderer.loadText(itemText);
            }
        }
    }

    private void renderTitle(float n, RequestInfo requestInfo, ObjectType titleType) {
        VAOLoader loader = renderer.getLoader();
        int titleTextureID = loader.loadGuiTexture(titleType.getTextureName());
        Vector2f position = requestInfo.getTexturePosition();
        Vector2f scale = requestInfo.getTextureScale();
        float height = scale.y / n;
        float titleY = position.y + scale.y - height;
        Vector2f titlePosition = new Vector2f(position.x, titleY);
        Vector2f titleScale = new Vector2f(scale.x, height);
        renderer.processGui(new GuiTexture(titleTextureID, titlePosition, titleScale, ObjectType.INVENTORY_TITLE));
    }

    private void renderGrid(float n, float m, RequestInfo requestInfo) {
        Vector2f scale = requestInfo.getTextureScale();
        Vector2f position = requestInfo.getTexturePosition();
        VAOLoader loader = renderer.getLoader();
        int slotTextureID = loader.loadGuiTexture(ObjectType.SLOT.getTextureName());
        int slotHoverTextureID = loader.loadGuiTexture(ObjectType.SLOT_HOVER.getTextureName());
        float slotWidth = (2 * scale.x) / m;
        float slotHeight = (2 * scale.y) / n;
        Vector2f upperLeftCorner = new Vector2f(position.x - scale.x, position.y + scale.y - slotHeight);
        Vector2f slotScale = new Vector2f(slotWidth / 2, slotHeight / 2);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                Vector2f slotPosition = new Vector2f(MathUtil.roundFloat(upperLeftCorner.x + (j + 0.5f) * slotWidth, 4),
                        MathUtil.roundFloat(upperLeftCorner.y - (i + 0.5f) * slotHeight, 4));
                Slot slot;
                if (requestInfo.getGuiType() == ObjectType.INVENTORY) {
                    slot = player.getInventory().initSlot(slotTextureID, slotHoverTextureID, slotPosition, slotScale, i * (int) m + j);
                }
                else {
                    LootableEntity currentLootable = HandlerState.getInstance().getLastLooted();
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
