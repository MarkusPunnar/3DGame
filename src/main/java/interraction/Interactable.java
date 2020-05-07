package interraction;

import game.state.GameState;
import org.joml.Vector3f;

public interface Interactable {

    void interact();

    GameState handleGui(GameState state);

    Vector3f getPosition();

    float getInteractionTime();

    void setInteractionTime(float time);
}
