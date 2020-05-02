package object.scene;

import model.TexturedModel;
import object.Entity;
import interraction.Interactable;
import org.joml.Vector3f;

public class Door extends Entity implements Interactable {

    private boolean isOpened;
    private float sinceLastInteraction;

    public Door(TexturedModel texturedModel, Vector3f position, float rotationX, float rotationY, float rotationZ, Vector3f scaleVector) {
        super(texturedModel, position, rotationX, rotationY, rotationZ, scaleVector);
        isOpened = false;
        sinceLastInteraction = Float.MAX_VALUE;
    }

    @Override
    public void interact() {
        if (sinceLastInteraction > 0.5f) {
            if (!isOpened) {
                increaseRotation(0, 90, 0);
                increasePosition(-7, 0, 6);
                isOpened = true;
                sinceLastInteraction = 0f;
            }
            else {
                increaseRotation(0, -90, 0);
                increasePosition(7, 0, -6);
                isOpened = false;
                sinceLastInteraction = 0f;
            }
        }
    }

    public float getInteractionTime() {
        return sinceLastInteraction;
    }

    public void setInteractionTime(float time) {
        this.sinceLastInteraction = time;
    }
}
