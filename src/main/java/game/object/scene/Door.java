package game.object.scene;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.flogger.FluentLogger;
import engine.font.GUIText;
import engine.model.TexturedModel;
import engine.render.request.GuiRenderRequest;
import engine.render.request.RequestType;
import game.interraction.InteractableEntity;
import game.object.generation.EntityLoader;
import game.object.generation.GenerationUtil;
import game.state.Game;
import game.state.HandlerState;
import game.ui.ObjectType;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Map;

public class Door extends InteractableEntity {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private FacingDirection facingDirection;
    private boolean isLocked;

    private Door(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scaleVector, FacingDirection direction, boolean isLocked) {
        super(model, position, rotation, scaleVector);
        this.facingDirection = direction;
        this.isLocked = isLocked;
    }

    public static Door build(TexturedModel model, JsonNode attributeNode, Map<String, Vector3f> objectData) {
        JsonNode positionNode = attributeNode.get("position");
        Vector3f rotation = attributeNode.has("rotation") ? EntityLoader.getVectorFromNode(attributeNode.get("rotation")) : new Vector3f();
        FacingDirection direction = getDirectionFromString(attributeNode.get("facing").asText());
        boolean isLocked = attributeNode.get("locked").asBoolean();
        Vector3f scale = objectData.containsKey("scale") ? objectData.get("scale") : new Vector3f(1);
        return new Door(model, new Vector3f(EntityLoader.getVectorFromNode(positionNode)), rotation, scale, direction, isLocked);
    }

    private static FacingDirection getDirectionFromString(String direction) {
        switch (direction) {
            case "west":
                return FacingDirection.WEST;
            case "east":
                return FacingDirection.EAST;
            case "north":
                return FacingDirection.NORTH;
            case "south":
                return FacingDirection.SOUTH;
        }
        logger.atWarning().log("Unknown direction %s", direction);
        return null;
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
                    .position(GenerationUtil.fromOpenGLCoords(-1, -0.65f)).build();
            HandlerState.getInstance().registerRequest(new GuiRenderRequest.Builder(RequestType.ADD, ObjectType.GUI).position(new Vector2f(0, -0.7f))
                    .scale(new Vector2f(0.6f, 0.1f)).name("slot").lifetime(1).withText(lockedText).build());
        }
    }
}
