package object.scene;

import engine.render.RenderRequest;
import game.state.GameState;
import game.state.HandlerState;
import interraction.handle.RenderRequestHandler;
import engine.render.RequestInfo;
import engine.render.RequestType;
import engine.texture.GuiType;
import interraction.Interactable;
import engine.model.TexturedModel;
import object.Entity;
import object.item.Slot;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Chest extends Entity implements Interactable {

    private boolean isOpened;
    private float sinceLastInteraction;
    private TexturedModel openModel;
    private TexturedModel closedModel;
    private Slot[] content;

    public Chest(TexturedModel texturedModel, Vector3f position, float rotationX, float rotationY, float rotationZ, Vector3f scaleVector, TexturedModel openModel, TexturedModel closedModel) {
        super(texturedModel, position, rotationX, rotationY, rotationZ, scaleVector);
        this.isOpened = false;
        this.openModel = openModel;
        this.closedModel = closedModel;
        this.sinceLastInteraction = Float.MAX_VALUE;
        this.content = new Slot[5];
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
        if (isOpened) {
            handlerState.registerRequest(new RenderRequest(RequestType.ADD, new RequestInfo("purple", new Vector2f(-0.5f, 0f), new Vector2f(0.4f, 0.8f), GuiType.INVENTORY)));
            handlerState.registerRequest(new RenderRequest(RequestType.ADD, new RequestInfo("white", new Vector2f(0.5f, 0f), new Vector2f(0.4f, 0.8f), GuiType.CHEST)));
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
}
