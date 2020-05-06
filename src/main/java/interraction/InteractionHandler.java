package interraction;

import object.Player;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import renderEngine.DisplayManager;

import java.util.ArrayList;
import java.util.List;

public class InteractionHandler {

    private final float INTERACT_DISTANCE = 20f;

    private List<Interactable> interactableObjects;
    private Player player;

    public InteractionHandler(Player player) {
        interactableObjects = new ArrayList<>();
        this.player = player;
    }

    public void addObject(Interactable object) {
        interactableObjects.add(object);
    }

    public void addObjects(List<Interactable> objects) {
        interactableObjects.addAll(objects);
    }

    public void checkInteractions() {
        Vector3f playerPosition = player.getPosition();
        Interactable closest = null;
        float closestDistance = Float.MAX_VALUE;
        for (Interactable object : interactableObjects) {
            Vector3f objectPosition = object.getPosition();
            float distance = objectPosition.distance(playerPosition);
            if (distance < closestDistance) {
                closest = object;
                closestDistance = distance;
            }
            object.setInteractionTime(object.getInteractionTime() + DisplayManager.getFrameTime());
        }
        int fState = GLFW.glfwGetKey(DisplayManager.getWindow(), GLFW.GLFW_KEY_F);
        if (closestDistance < INTERACT_DISTANCE && fState == GLFW.GLFW_PRESS) {
            closest.interact();
            closest.openGui();
        }
    }
}
