package object.scene;

import engine.model.TexturedModel;
import game.state.GameState;
import object.Entity;
import interraction.Interactable;
import util.FacingDirection;
import org.joml.Vector3f;

public class Door extends Entity implements Interactable {

    private boolean isOpened;
    private float sinceLastInteraction;
    private FacingDirection facingDirection;

    public Door(TexturedModel texturedModel, Vector3f position, float rotationX, float rotationY, float rotationZ, Vector3f scaleVector, FacingDirection direction) {
        super(texturedModel, position, rotationX, rotationY, rotationZ, scaleVector);
        this.isOpened = false;
        this.sinceLastInteraction = Float.MAX_VALUE;
        this.facingDirection = direction;
    }

    @Override
    public void interact() {
        if (sinceLastInteraction > 0.2f) {
            if (!isOpened) {
                switch (facingDirection) {
                    case WEST:
                        increaseRotation(0, 90, 0);
                        increasePosition(-7, 0, 6.5f);
                        break;
                    case EAST:
                        increaseRotation(0, -90, 0);
                        increasePosition(7, 0, 6.5f);
                        break;
                    default:
                }
                isOpened = true;
                sinceLastInteraction = 0f;
            }
            else {
                switch (facingDirection) {
                    case WEST:
                        increaseRotation(0, -90, 0);
                        increasePosition(7, 0, -6.5f);
                        break;
                    case EAST:
                        increaseRotation(0, 90, 0);
                        increasePosition(-7, 0, -6.5f);
                        break;
                    default:
                }
                isOpened = false;
                sinceLastInteraction = 0f;
            }
        }
    }

    @Override
    public GameState handleGui(GameState state) {
        return state;
    }

    public float getInteractionTime() {
        return sinceLastInteraction;
    }

    public void setInteractionTime(float time) {
        this.sinceLastInteraction = time;
    }
}
