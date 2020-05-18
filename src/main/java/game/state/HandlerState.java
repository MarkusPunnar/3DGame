package game.state;

import engine.render.RenderRequest;
import interraction.InteractableEntity;
import interraction.LootableEntity;
import object.item.Item;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HandlerState {

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
        requests.add(request);
    }

    public void registerInteractableEntity(InteractableEntity object) {
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
