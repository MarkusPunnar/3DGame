package util;

import com.google.common.flogger.FluentLogger;
import engine.DisplayManager;
import engine.font.GUIText;
import engine.render.RenderRequest;
import engine.render.RequestInfo;
import engine.render.RequestType;
import engine.texture.GuiTexture;
import game.state.HandlerState;
import interraction.MousePicker;
import object.item.Item;
import object.item.Slot;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class HandlerUtil {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static boolean leftMouseButtonPressed = false;
    private static Slot lastInteracted;

    private static Slot calculateActiveSlot(Slot[] contents, Vector2f currentMousePosition) {
        Slot activeSlot = null;
        for (Slot content : contents) {
            boolean xMatch = Math.abs(content.getPosition().x - currentMousePosition.x) < content.getScaleVector().x;
            boolean yMatch = Math.abs(content.getPosition().y - currentMousePosition.y) < content.getScaleVector().y;
            if (xMatch && yMatch) {
                content.changeTexture();
                activeSlot = content;
            } else {
                content.resetTexture();
            }
        }
        return activeSlot;
    }

    public static void moveItems(Slot[] source, Slot[] destination, MousePicker mousePicker) {
        Vector2f currentMousePosition = mousePicker.calculateDeviceCoords();
        Item bindedItem = HandlerState.getInstance().getBindedItem();
        int leftMouseButtonState = GLFW.glfwGetMouseButton(DisplayManager.getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT);
        if (leftMouseButtonState == GLFW.GLFW_PRESS) {
            leftMouseButtonPressed = true;
        } else if (leftMouseButtonState == GLFW.GLFW_RELEASE) {
            leftMouseButtonPressed = false;
        }
        if (bindedItem == null) {
            Slot activeSlot = HandlerUtil.calculateActiveSlot(source, currentMousePosition);
            if (activeSlot != null) {
                if (leftMouseButtonPressed) {
                    HandlerState.getInstance().setBindedItem(activeSlot.getItem());
                    logger.atInfo().log("Binded item of type %s for moving", activeSlot.getItem().getClass().getSimpleName());
                    lastInteracted = activeSlot;
                    activeSlot.resetTexture();
                }
            }
        }
        else {
            if (leftMouseButtonPressed) {
                //Move item
                bindedItem.getIcon().setPosition(currentMousePosition);
                setItemText(lastInteracted, bindedItem, new Vector3f(currentMousePosition.x, currentMousePosition.y, 0));
            } else {
                placeItem(source, destination, currentMousePosition, bindedItem);
            }
        }
    }

    private static void placeItem(Slot[] source, Slot[] destination, Vector2f currentMousePosition, Item bindedItem) {
        Slot activeSlot = HandlerUtil.calculateActiveSlot(destination, currentMousePosition);
        Slot otherSlot = HandlerUtil.calculateActiveSlot(source, currentMousePosition);
        if (activeSlot == null && otherSlot == null) {
            bindedItem.getIcon().setPosition(new Vector2f(lastInteracted.getPosition().x, lastInteracted.getPosition().y));
            setItemText(lastInteracted, bindedItem, lastInteracted.getPosition());
            HandlerState.getInstance().setBindedItem(null);
            logger.atInfo().log("Misplaced item, moving back to original location");
            return;
        }
        Slot selectedSlot = activeSlot == null ? otherSlot : activeSlot;
        if (selectedSlot.getItem() == null) {
            selectedSlot.setItem(bindedItem);
            bindedItem.getIcon().setPosition(new Vector2f(selectedSlot.getPosition().x, selectedSlot.getPosition().y));
            setItemText(lastInteracted, bindedItem, selectedSlot.getPosition());
            selectedSlot.setGuiText(lastInteracted.getGuiText());
            logger.atInfo().log("Moved item %s successfully", lastInteracted.getItem().getClass().getSimpleName());
            lastInteracted.setItem(null);
            lastInteracted.setGuiText(null);
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
            existingItem.stack(lastInteracted.getItem());
            GUIText newText = lastInteracted.getGuiText().copyWithValueChange(String.valueOf(existingItem.getAmount()));
            logger.atInfo().log("Sending requests to remove old item icons and text");
            handlerState.registerRequest(new RenderRequest(RequestType.REMOVE_TEXT, new RequestInfo(selectedSlot.getItem().getIcon(), selectedSlot.getGuiText())));
            selectedSlot.setGuiText(newText);
            setItemText(selectedSlot, existingItem, selectedSlot.getPosition());
            handlerState.registerRequest(new RenderRequest(RequestType.REMOVE_ITEM, new RequestInfo(lastInteracted.getItem().getIcon(), lastInteracted.getGuiText())));
            handlerState.registerRequest(new RenderRequest(RequestType.REFRESH_TEXT,  new RequestInfo(selectedSlot.getItem().getIcon(), selectedSlot.getGuiText())));
            lastInteracted.setItem(null);
            lastInteracted.setGuiText(null);
        }
        else {
            bindedItem.getIcon().setPosition(new Vector2f(lastInteracted.getPosition().x, lastInteracted.getPosition().y));
            setItemText(lastInteracted, bindedItem, selectedSlot.getPosition());
        }
    }

    private static void setItemText(GuiTexture gui, Item item, Vector3f coords) {
        gui.getGuiText().setPosition( new Vector2f((1 + coords.x) / 2f + item.getPaddingX(),  Math.abs(coords.y - 1) / 2f + item.getPaddingY()));
    }
}
