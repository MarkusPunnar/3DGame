package game.object.scene;

import com.google.common.flogger.FluentLogger;
import engine.model.TexturedModel;
import engine.render.request.GuiRenderRequest;
import engine.render.request.RequestType;
import game.interraction.LootableEntity;
import game.object.item.Item;
import game.object.item.Slot;
import game.state.Game;
import game.state.HandlerState;
import game.ui.ObjectType;
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

        @Override
        public Builder rotationY(float rotationY) {
            super.rotationY(rotationY);
            return this;
        }

        @Override
        public Builder scale(Vector3f scale) {
            super.scale(scale);
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
            handlerState.registerRequest(new GuiRenderRequest(RequestType.ADD, ObjectType.INVENTORY, new Vector2f(-0.55f, -0.2f), new Vector2f(1f, 1.2f)));
            handlerState.registerRequest(new GuiRenderRequest(RequestType.ADD, ObjectType.CHEST, new Vector2f(0.45f, -0.2f), new Vector2f(1f, 1.2f)));
        } else {
            handlerState.registerRequest(new GuiRenderRequest(RequestType.REMOVE, ObjectType.INVENTORY));
            handlerState.registerRequest(new GuiRenderRequest(RequestType.REMOVE, ObjectType.CHEST));
        }
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
