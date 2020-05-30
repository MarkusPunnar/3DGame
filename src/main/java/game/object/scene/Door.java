package game.object.scene;

import com.google.common.flogger.FluentLogger;
import engine.model.TexturedModel;
import game.state.Game;
import game.interraction.InteractableEntity;
import org.joml.Vector3f;
import util.FacingDirection;

public class Door extends InteractableEntity {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

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
                logger.atInfo().log("Opened door with facing direction %s", facingDirection);
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
                logger.atInfo().log("Closed door with facing direction %s", facingDirection);
            }
        }
    }

    @Override
    public void handleGui(Game state) {
    }
}
