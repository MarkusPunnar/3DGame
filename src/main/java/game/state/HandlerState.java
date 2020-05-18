package game.state;

import engine.render.RenderRequest;
import interraction.Interactable;
import interraction.Lootable;
import object.item.Item;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HandlerState {

    private static final HandlerState INSTANCE = new HandlerState();

    private Queue<RenderRequest> requests;
    private List<Interactable> interactableEntities;
    private Lootable lastLooted;
    private Item bindedItem;
    private Interactable closestObject;

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

    public void registerInteractableEntity(Interactable object) {
        interactableEntities.add(object);
    }

    public Queue<RenderRequest> getRequests() {
        return requests;
    }

    public List<Interactable> getInteractableEntities() {
        return interactableEntities;
    }

    public Lootable getLastLooted() {
        return lastLooted;
    }

    public void setLastLooted(Lootable lastLooted) {
        this.lastLooted = lastLooted;
    }

    public Item getBindedItem() {
        return bindedItem;
    }

    public void setBindedItem(Item bindedItem) {
        this.bindedItem = bindedItem;
    }

    public Interactable getClosestObject() {
        return closestObject;
    }

    public void setClosestObject(Interactable closestObject) {
        this.closestObject = closestObject;
    }
}
