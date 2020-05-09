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

    private Queue<RenderRequest> requests;
    private List<Interactable> interactableObjects;
    private Lootable lastLooted;
    private Item bindedItem;
    private Interactable closestObject;

    public HandlerState() {
        requests = new LinkedList<>();
        interactableObjects = new ArrayList<>();
    }

    public void registerRequest(RenderRequest request) {
        requests.add(request);
    }

    public void registerInteractableObject(Interactable object) {
        interactableObjects.add(object);
    }

    public Queue<RenderRequest> getRequests() {
        return requests;
    }

    public List<Interactable> getInteractableObjects() {
        return interactableObjects;
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
