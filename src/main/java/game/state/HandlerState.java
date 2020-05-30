package game.state;

import com.google.common.flogger.FluentLogger;
import engine.render.RenderRequest;
import game.interraction.InteractableEntity;
import game.interraction.LootableEntity;
import game.object.item.Item;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HandlerState {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static final HandlerState INSTANCE = new HandlerState();

    private Queue<RenderRequest> requests;
    private List<InteractableEntity> interactableEntities;
    private LootableEntity lastLooted;
    private Item bindedItem;
    private InteractableEntity closestObject;

    public HandlerState() {
        requests = new LinkedList<>();
        interactableEntities = new ArrayList<>();
    }

    public static HandlerState getInstance() {
        return INSTANCE;
    }

    public void registerRequest(RenderRequest request) {
        logger.atInfo().log("Registered new render request of type %s", request.toString());
        requests.add(request);
    }

    public void registerInteractableEntity(InteractableEntity object) {
        logger.atInfo().log("Registered new interactable entity with type %s", object.getClass().getSimpleName());
        interactableEntities.add(object);
    }

    public Queue<RenderRequest> getRequests() {
        return requests;
    }

    public List<InteractableEntity> getInteractableEntities() {
        return interactableEntities;
    }

    public LootableEntity getLastLooted() {
        return lastLooted;
    }

    public void setLastLooted(LootableEntity lastLooted) {
        this.lastLooted = lastLooted;
    }

    public Item getBindedItem() {
        return bindedItem;
    }

    public void setBindedItem(Item bindedItem) {
        this.bindedItem = bindedItem;
    }

    public InteractableEntity getClosestObject() {
        return closestObject;
    }

    public void setClosestObject(InteractableEntity closestObject) {
        this.closestObject = closestObject;
    }
}
