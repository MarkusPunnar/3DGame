package game.state;

import engine.render.RenderRequest;
import interraction.Interactable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HandlerState {

    private Queue<RenderRequest> requests;
    private List<Interactable> interactableObjects;

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
}
