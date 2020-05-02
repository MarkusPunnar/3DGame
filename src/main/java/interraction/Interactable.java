package interraction;

import org.joml.Vector3f;

public interface Interactable {

    void interact();

    Vector3f getPosition();

    float getInteractionTime();

    void setInteractionTime(float time);
}
