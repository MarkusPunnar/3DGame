package object.scene;

import engine.model.TexturedModel;
import game.state.GameState;
import interraction.InteractableEntity;
import org.joml.Vector3f;
import util.FacingDirection;

public class Door extends InteractableEntity {

    private FacingDirection facingDirection;

    private Door(Builder builder) {
        super(builder);
        this.facingDirection = builder.facingDirection;
    }

    public static class Builder extends InteractableEntity.Builder {

        private FacingDirection facingDirection = FacingDirection.WEST;

        public Builder(TexturedModel texturedModel, Vector3f position) {
            super(texturedModel, position);
        }

        public Builder facing(FacingDirection direction) {
            this.facingDirection = direction;
            return this;
        }

        public Door build() {
            return new Door(this);
        }

        public Builder self() {
            return this;
        }
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
}
