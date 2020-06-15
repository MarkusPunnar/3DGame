package game.object.scene;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.flogger.FluentLogger;
import engine.model.TexturedModel;
import engine.render.request.GuiRenderRequest;
import engine.render.request.RequestType;
import game.interraction.LootableEntity;
import game.object.generation.EntityLoader;
import game.object.item.Item;
import game.object.item.Slot;
import game.state.Game;
import game.state.HandlerState;
import game.ui.ObjectType;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Map;

public class Chest extends LootableEntity {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private TexturedModel openModel;
    private TexturedModel closedModel;

    public Chest(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scaleVector, TexturedModel secondaryModel, int capacity) {
        super(model, position, rotation, scaleVector, capacity);
        this.openModel = secondaryModel;
        this.closedModel = model;
    }

    public static Chest build(TexturedModel closedModel, TexturedModel openModel, JsonNode attributeNode, Map<String, Vector3f> objectData) {
        JsonNode positionNode = attributeNode.get("position");
        Vector3f rotation = attributeNode.has("rotation") ? EntityLoader.getVectorFromNode(attributeNode.get("rotation")) : new Vector3f();
        int capacity = attributeNode.get("capacity").asInt();
        Vector3f scale = objectData.containsKey("scale") ? objectData.get("scale") : new Vector3f(1);
        return new Chest(closedModel, new Vector3f(EntityLoader.getVectorFromNode(positionNode)), rotation, scale, openModel, capacity);
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
            handlerState.registerRequest(new GuiRenderRequest.Builder(RequestType.ADD, ObjectType.INVENTORY).position(new Vector2f(-0.55f, -0.2f))
                    .scale(new Vector2f(1f, 1.2f)).build());
            handlerState.registerRequest(new GuiRenderRequest.Builder(RequestType.ADD, ObjectType.CHEST).position(new Vector2f(0.45f, -0.2f))
                    .scale(new Vector2f(1f, 1.2f)).build());
        } else {
            handlerState.registerRequest(new GuiRenderRequest.Builder(RequestType.REMOVE, ObjectType.INVENTORY).build());
            handlerState.registerRequest(new GuiRenderRequest.Builder(RequestType.REMOVE, ObjectType.CHEST).build());
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
