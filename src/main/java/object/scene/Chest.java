package object.scene;

import com.google.common.flogger.FluentLogger;
import engine.render.RenderRequest;
import game.state.Game;
import game.state.HandlerState;
import engine.render.RequestInfo;
import engine.render.RequestType;
import game.ui.ObjectType;
import engine.model.TexturedModel;
import interraction.LootableEntity;
import object.item.Icon;
import object.item.Item;
import object.item.Slot;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Chest extends LootableEntity {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private TexturedModel openModel;
    private TexturedModel closedModel;

    public Chest(Builder builder) {
        super(builder);
        this.openModel = builder.openModel;
        this.closedModel = builder.closedModel;
    }

    public static class Builder extends LootableEntity.Builder {

        private final TexturedModel openModel;
        private final TexturedModel closedModel;

        public Builder(TexturedModel closedModel, Vector3f position, TexturedModel openModel) {
            super(closedModel, position);
            this.openModel = openModel;
            this.closedModel = closedModel;
        }

        @Override
        public Builder capacity(int capacity) {
            super.capacity(capacity);
            return this;
        }

        public Chest build() {
            return new Chest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    @Override
    public void interact() {
        if (sinceLastInteraction > 0.5f) {
            if (!isOpened) {
                setTexturedModel(openModel);
                logger.atInfo().log("Chest opened");
                isOpened = true;
                sinceLastInteraction = 0;
            } else {
                setTexturedModel(closedModel);
                isOpened = false;
                logger.atInfo().log("Chest closed");
                sinceLastInteraction = 0;
            }
        }
    }

    @Override
    public void handleGui(Game state) {
        HandlerState handlerState = HandlerState.getInstance();
        handlerState.setLastLooted(this);
        logger.atInfo().log("Handling GUI changes for object of type %s", getClass().getSimpleName());
        if (isOpened) {
            handlerState.registerRequest(new RenderRequest(RequestType.ADD, new RequestInfo(new Vector2f(-0.5f, 0.2f), new Vector2f(0.4f, 0.6f), ObjectType.INVENTORY)));
            handlerState.registerRequest(new RenderRequest(RequestType.ADD, new RequestInfo(new Vector2f(0.5f, 0.2f), new Vector2f(0.4f, 0.6f), ObjectType.CHEST)));
        } else {
            handlerState.registerRequest(new RenderRequest(RequestType.REMOVE, new RequestInfo(ObjectType.INVENTORY)));
            handlerState.registerRequest(new RenderRequest(RequestType.REMOVE, new RequestInfo(ObjectType.CHEST)));
        }
    }


    public Slot initSlot(int textureID, int hoverID, Vector2f position, Vector2f scale, int index) {
        Slot slot = content[index];
        slot.setID(textureID);
        slot.setNormalTextureID(textureID);
        slot.setHoverTextureID(hoverID);
        slot.setPosition(position);
        slot.setScale(scale);
        slot.setType(ObjectType.SLOT);
        if (slot.getItem() != null) {
            Icon itemIcon = slot.getItem().getIcon();
            itemIcon.setPosition(new Vector2f(slot.getPosition().x, slot.getPosition().y));
            itemIcon.setScale(new Vector2f(slot.getScaleVector().x / 1.3f, slot.getScaleVector().y / 1.3f));
        }
        return slot;
    }

    public boolean addItem(Item item) {
        for (Slot slot : content) {
            if (slot.isFree()) {
                slot.setItem(item);
                logger.atInfo().log("Added %s to chest", item.getClass().getSimpleName());
                return true;
            }
        }
        return false;
    }
}
