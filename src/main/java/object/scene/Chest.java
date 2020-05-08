package object.scene;

import engine.render.RenderRequest;
import game.state.GameState;
import game.state.HandlerState;
import engine.render.RequestInfo;
import engine.render.RequestType;
import engine.texture.GuiType;
import interraction.Interactable;
import engine.model.TexturedModel;
import interraction.Lootable;
import object.Entity;
import object.item.Icon;
import object.item.Item;
import object.item.Slot;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Chest extends Entity implements Interactable, Lootable {

    private boolean isOpened;
    private float sinceLastInteraction;
    private TexturedModel openModel;
    private TexturedModel closedModel;
    private Slot[] content;

    public Chest(TexturedModel texturedModel, Vector3f position, Vector3f rotation, Vector3f scaleVector, TexturedModel openModel, TexturedModel closedModel) {
        super(texturedModel, position, rotation, scaleVector);
        this.isOpened = false;
        this.openModel = openModel;
        this.closedModel = closedModel;
        this.sinceLastInteraction = Float.MAX_VALUE;
        this.content = new Slot[20];
        for (int i = 0; i < content.length; i++) {
            content[i] = new Slot();
        }
    }

    @Override
    public void interact() {
        if (sinceLastInteraction > 0.2f) {
            if (!isOpened) {
                setTexturedModel(openModel);
                isOpened = true;
                sinceLastInteraction = 0;
            } else {
                setTexturedModel(closedModel);
                isOpened = false;
                sinceLastInteraction = 0;
            }
        }
    }

    @Override
    public GameState handleGui(GameState state) {
        HandlerState handlerState = state.getHandlerState();
        handlerState.setLastLooted(this);
        if (isOpened) {
            handlerState.registerRequest(new RenderRequest(RequestType.ADD, new RequestInfo(new Vector2f(-0.5f, 0.2f), new Vector2f(0.4f, 0.6f), GuiType.INVENTORY)));
            handlerState.registerRequest(new RenderRequest(RequestType.ADD, new RequestInfo(new Vector2f(0.5f, 0.2f), new Vector2f(0.4f, 0.6f), GuiType.CHEST)));
        } else {
            handlerState.registerRequest(new RenderRequest(RequestType.REMOVE, new RequestInfo(GuiType.INVENTORY)));
            handlerState.registerRequest(new RenderRequest(RequestType.REMOVE, new RequestInfo(GuiType.CHEST)));
        }
        return state;
    }

    @Override
    public float getInteractionTime() {
        return sinceLastInteraction;
    }

    @Override
    public void setInteractionTime(float time) {
        this.sinceLastInteraction = time;
    }

    public Slot[] getContent() {
        return content;
    }

    public void initSlot(int textureID, int hoverID, Vector2f position, Vector2f scale, int index) {
        Slot slot = content[index];
        slot.setID(textureID);
        slot.setNormalTextureID(textureID);
        slot.setHoverTextureID(hoverID);
        slot.setPosition(position);
        slot.setScale(scale);
        if (slot.getItem() != null) {
            Icon itemIcon = slot.getItem().getIcon();
            itemIcon.setPosition(new Vector2f(slot.getPosition().x, slot.getPosition().y));
            itemIcon.setScale(new Vector2f(slot.getScaleVector().x / 1.3f, slot.getScaleVector().y / 1.3f));
        }
    }

    public boolean addItem(Item item) {
        for (Slot slot : content) {
            if (slot.isFree()) {
                slot.setItem(item);
                return true;
            }
        }
        return false;
    }
}
