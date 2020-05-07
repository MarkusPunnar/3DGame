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
import game.MainGameLoop;
import object.Player;
import object.item.Slot;
import org.joml.Vector2f;
import util.math.MathUtil;

import java.util.ArrayList;
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
                            renderInventory(requestInfo.getTexturePosition(), requestInfo.getTextureScale());
                            break;
                        case CHEST:
                            break;
                        default:
                    }
                    break;
                case REMOVE:
                    switch (requestInfo.getGuiType()) {
                        case INVENTORY:
                            removeInventory();
                            state.setCurrentState(State.IN_GAME);
                            break;
                        case CHEST:
                            break;
                        default:
                    }
                    break;
                default:
            }
        }
        return state;
    }

    private void removeInventory() {
        for (RenderObject object : new ArrayList<>(renderer.getGuis())) {
            if (object instanceof GuiTexture) {
                GuiTexture gui = ((GuiTexture) object);
                if (gui.getType() == GuiType.SLOT || gui.getType() == GuiType.INVENTORY_TITLE) {
                    renderer.getGuis().remove(object);
                }
            }
        }
    }

    private void renderInventory(Vector2f position, Vector2f scale) {
        float n = 5;
        float m = 5;
        //render nxm grid
        Loader loader = renderer.getLoader();
        int slotTextureID = loader.loadTexture(GuiType.SLOT.getTextureName());
        int slotHoverTextureID = loader.loadTexture(GuiType.SLOT_HOVER.getTextureName());
        int titleTextureID = loader.loadTexture(GuiType.INVENTORY_TITLE.getTextureName());
        Vector2f upperLeftCorner = new Vector2f(position.x - scale.x, position.y + scale.y);

        float slotWidth = (2 * scale.x) / m;
        float slotHeight = (2 * scale.y) / n;
        Vector2f slotScale = new Vector2f(slotWidth / 2, slotHeight / 2);
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                Vector2f titlePosition = new Vector2f(position.x, upperLeftCorner.y - (i + 0.5f) * slotHeight);
                Vector2f titleScale = new Vector2f(scale.x, slotScale.y);
                renderer.processGui(new GuiTexture(titleTextureID, titlePosition, titleScale, GuiType.INVENTORY_TITLE));
            }
            else {
                for (int j = 0; j < m; j++) {
                    Vector2f slotPosition = new Vector2f(MathUtil.roundFloat(upperLeftCorner.x + (j + 0.5f) * slotWidth, 4),
                            MathUtil.roundFloat(upperLeftCorner.y - (i + 0.5f) * slotHeight, 4));
                    Slot inventorySlot = new Slot(slotTextureID, slotPosition, slotScale, GuiType.SLOT, slotHoverTextureID);
                    renderer.processGui(inventorySlot);
                    player.getInventory().registerSlot(inventorySlot, (i - 1) * (int) n + j);
                }
            }

        }
    }
}
