package game.interraction.handle;

import com.google.common.flogger.FluentLogger;
import engine.DisplayManager;
import engine.font.GUIText;
import engine.render.ParentRenderer;
import engine.render.request.GuiRenderRequest;
import engine.render.request.RequestType;
import game.object.RenderObject;
import game.object.item.Item;
import game.object.item.Slot;
import game.state.Game;
import game.state.HandlerState;
import game.ui.ObjectType;
import game.ui.UIComponent;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HandlerUtil {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static boolean leftMouseButtonPressed = false;
    private static Slot lastInteracted;

    private static Slot getActiveSlot(Slot[] contents, Vector2f currentMousePosition) {
        for (Slot content : contents) {
            boolean xMatch = Math.abs(content.getPosition().x - currentMousePosition.x) < content.getScale().x;
            boolean yMatch = Math.abs(content.getPosition().y - currentMousePosition.y) < content.getScale().y;
            if (xMatch && yMatch) {
                return content;
            }
        }
        return null;
    }

    protected static void moveItems(Slot[] source, Slot[] destination) throws IOException {
        Vector2f currentMousePosition = Game.getInstance().getMousePicker().calculateDeviceCoords();
        Item bindedItem = HandlerState.getInstance().getBindedItem();
        int leftMouseButtonState = GLFW.glfwGetMouseButton(DisplayManager.getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT);
        if (leftMouseButtonState == GLFW.GLFW_PRESS) {
            leftMouseButtonPressed = true;
        } else if (leftMouseButtonState == GLFW.GLFW_RELEASE) {
            leftMouseButtonPressed = false;
        }
        if (bindedItem == null) {
            Slot activeSlot = HandlerUtil.getActiveSlot(source, currentMousePosition);
            if (activeSlot != null) {
                Integer textureID = Game.getInstance().getTextureCache().getByName("hoverslot");
                if (textureID == null) {
                    textureID = Game.getInstance().getLoader().loadGuiTexture("hoverslot");
                    Game.getInstance().getTextureCache().addTexture("hoverslot", textureID);
                }
                UIComponent hoveredSlot = new UIComponent(textureID, activeSlot.getPosition(), activeSlot.getScale(), ObjectType.SLOT);
                hoveredSlot.setTransparent(true);
                Game.getInstance().getRenderer().getGuis().add(hoveredSlot);
                if (leftMouseButtonPressed) {
                    HandlerState.getInstance().setBindedItem(activeSlot.getItem());
                    if (activeSlot.getItem() != null) {
                        logger.atInfo().log("Binded item of type %s for moving", activeSlot.getItem().getClass().getSimpleName());
                    }
                    lastInteracted = activeSlot;
                }
            }
        } else {
            if (leftMouseButtonPressed) {
                //Move item
                bindedItem.getIcon().setPosition(currentMousePosition);
                setItemText(bindedItem, currentMousePosition);
            } else {
                placeItem(source, destination, currentMousePosition, bindedItem);
            }
        }
    }

    private static void placeItem(Slot[] source, Slot[] destination, Vector2f currentMousePosition, Item bindedItem) {
        Slot activeSlot = HandlerUtil.getActiveSlot(destination, currentMousePosition);
        Slot otherSlot = HandlerUtil.getActiveSlot(source, currentMousePosition);
        if (activeSlot == null && otherSlot == null) {
            bindedItem.getIcon().setPosition(new Vector2f(lastInteracted.getPosition().x, lastInteracted.getPosition().y));
            setItemText(bindedItem, lastInteracted.getPosition());
            HandlerState.getInstance().setBindedItem(null);
            logger.atInfo().log("Misplaced item, moving back to original location");
            return;
        }
        Slot selectedSlot = activeSlot == null ? otherSlot : activeSlot;
        if (selectedSlot.getItem() == null) {
            selectedSlot.setItem(bindedItem);
            bindedItem.getIcon().setPosition(new Vector2f(selectedSlot.getPosition().x, selectedSlot.getPosition().y));
            setItemText(bindedItem, selectedSlot.getPosition());
            selectedSlot.getItem().setGuiText(lastInteracted.getItem().getGuiText());
            logger.atInfo().log("Moved item %s successfully", lastInteracted.getItem().getClass().getSimpleName());
            lastInteracted.setItem(null);
        } else {
            stackItems(bindedItem, selectedSlot);
        }
        HandlerState.getInstance().setBindedItem(null);
    }

    private static void stackItems(Item bindedItem, Slot selectedSlot) {
        if (!selectedSlot.equals(lastInteracted)) {
            logger.atInfo().log("Detected item in selected slot, trying to stack");
            HandlerState handlerState = HandlerState.getInstance();
            Item existingItem = selectedSlot.getItem();
            Item otherItem = lastInteracted.getItem();
            existingItem.stack(otherItem);
            GUIText newText = otherItem.getGuiText().copyWithValueChange(String.valueOf(existingItem.getAmount()));
            logger.atInfo().log("Sending requests to remove old item icons and text");
            handlerState.registerRequest(new GuiRenderRequest.Builder(RequestType.REMOVE, ObjectType.TEXT)
                    .withText(selectedSlot.getItem().getGuiText())
                    .forObject(selectedSlot.getItem().getIcon()).build());
            existingItem.setGuiText(newText);
            setItemText(existingItem, selectedSlot.getPosition());
            handlerState.registerRequest(new GuiRenderRequest.Builder(RequestType.REMOVE, ObjectType.ICON)
                    .withText(otherItem.getGuiText())
                    .forObject(otherItem.getIcon()).build());
            handlerState.registerRequest(new GuiRenderRequest.Builder(RequestType.UPDATE, ObjectType.TEXT)
                    .withText(existingItem.getGuiText())
                    .forObject(existingItem.getGuiText()).build());
            lastInteracted.setItem(null);
            otherItem.setGuiText(null);
        } else {
            bindedItem.getIcon().setPosition(new Vector2f(lastInteracted.getPosition().x, lastInteracted.getPosition().y));
            setItemText(bindedItem, selectedSlot.getPosition());
        }
    }

    private static void setItemText(Item item, Vector2f coords) {
        item.getGuiText().setPosition(new Vector2f((1 + coords.x) / 2f + item.getIcon().getScaleVector().x * 0.15f,
                Math.abs(coords.y - 1) / 2f + item.getIcon().getScaleVector().y * 0.2f));
    }


    public static void removeGui(List<ObjectType> typesToRemove) {
        ParentRenderer renderer = Game.getInstance().getRenderer();
        for (RenderObject object : new ArrayList<>(renderer.getGuis())) {
            if (typesToRemove.contains(object.getType())) {
                renderer.getGuis().remove(object);
                if (object.getGuiText() != null) {
                    renderer.removeText(object.getGuiText());
                }
            }
        }
    }
}
