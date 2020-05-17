package object.scene;

import engine.model.TexturedModel;
import game.state.GameState;
import interraction.InteractableEntity;
import util.FacingDirection;
import org.joml.Vector3f;
import util.octree.OctTree;

public class Door extends InteractableEntity {

    private boolean isOpened;
    private float sinceLastInteraction;
    private FacingDirection facingDirection;

    public Door(TexturedModel texturedModel, Vector3f position, Vector3f rotation, Vector3f scaleVector, FacingDirection direction) {
        super(texturedModel, position, rotation, scaleVector);
        this.isOpened = false;
        this.sinceLastInteraction = Float.MAX_VALUE;
        this.facingDirection = direction;
    }

    @Override
    public void interact() {
        if (sinceLastInteraction > 0.5f) {
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
    public void handleGui(GameState state) {
    }

    public float getInteractionTime() {
        return sinceLastInteraction;
    }

    public void setInteractionTime(float time) {
        this.sinceLastInteraction = time;
    }
}
