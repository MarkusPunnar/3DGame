package game.object.scene;

import com.google.common.flogger.FluentLogger;
import engine.font.GUIText;
import engine.model.TexturedModel;
import engine.render.request.GuiRenderRequest;
import engine.render.request.ItemRenderRequest;
import engine.render.request.RequestType;
import game.interraction.InteractableEntity;
import game.interraction.handle.Handler;
import game.object.generation.GeneratorUtil;
import game.state.Game;
import game.state.HandlerState;
import game.ui.ObjectType;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Door extends InteractableEntity {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private FacingDirection facingDirection;
    private boolean isLocked;

    private Door(Builder builder) {
        super(builder);
        this.facingDirection = builder.facingDirection;
        this.isLocked = builder.isLocked;
    }

    public static class Builder extends InteractableEntity.Builder {

        private FacingDirection facingDirection = FacingDirection.WEST;
        private boolean isLocked = false;

        public Builder(TexturedModel texturedModel, Vector3f position) {
            super(texturedModel, position);
        }

        public Builder facing(FacingDirection direction) {
            this.facingDirection = direction;
            return this;
        }

        public Builder locked(boolean isLocked) {
            this.isLocked = isLocked;
            return this;
        }

        @Override
        public Builder scale(Vector3f scale) {
            super.scale(scale);
            return this;
        }

        @Override
        public Builder rotationY(float rotationY) {
            super.rotationY(rotationY);
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
        if (sinceLastInteraction > 0.5f && !isLocked) {
            if (!isOpened) {
                switch (facingDirection) {
                    case WEST:
                        increaseRotation(0, 90, 0);
                        increasePosition(-7 * getScaleVector().x, 0, 6.5f * getScaleVector().z);
                        break;
                    case EAST:
                        increaseRotation(0, -90, 0);
                        increasePosition(7 * getScaleVector().x, 0, 6.5f * getScaleVector().z);
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
                        increasePosition(7 * getScaleVector().x, 0, -6.5f * getScaleVector().z);
                        break;
                    case EAST:
                        increaseRotation(0, 90, 0);
                        increasePosition(-7 * getScaleVector().x, 0, -6.5f * getScaleVector().z);
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
        if (isLocked) {
            GUIText lockedText = new GUIText.Builder("This door seems to be locked...").centered(true)
                    .position(GeneratorUtil.fromOpenGLCoords(-1, -0.65f)).build();
            HandlerState.getInstance().registerRequest(new GuiRenderRequest(RequestType.ADD, ObjectType.GUI,
                    new Vector2f(0, -0.7f), new Vector2f(0.6f, 0.1f), "slot"));
            HandlerState.getInstance().registerRequest(new ItemRenderRequest(RequestType.ADD, ObjectType.TEXT, null, lockedText));
        }
    }
}
