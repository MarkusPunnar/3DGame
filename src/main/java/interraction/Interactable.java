package interraction;

import game.state.GameState;
import object.RenderObject;

public interface Interactable extends RenderObject {

    void interact();

    void handleGui(GameState state);

    float getInteractionTime();

    void setInteractionTime(float time);
}
