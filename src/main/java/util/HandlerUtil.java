package util;

import engine.DisplayManager;
import game.state.GameState;
import interraction.MousePicker;
import object.item.Item;
import object.item.Slot;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class HandlerUtil {

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

    public static GameState moveItems(Slot[] source, Slot[] destination, GameState state, MousePicker mousePicker) {
        Vector2f currentMousePosition = mousePicker.calculateDeviceCoords();
        Item bindedItem = state.getHandlerState().getBindedItem();
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
                    state.getHandlerState().setBindedItem(activeSlot.getItem());
                    lastInteracted = activeSlot;
                }
            }
        }
        else {
            if (leftMouseButtonPressed) {
                bindedItem.getIcon().setPosition(currentMousePosition);
            } else {
                Slot activeSlot = HandlerUtil.calculateActiveSlot(destination, currentMousePosition);
                Slot otherSlot = HandlerUtil.calculateActiveSlot(source, currentMousePosition);
                if (activeSlot == null && otherSlot == null) {
                    //Misplaced item
                    bindedItem.getIcon().setPosition(new Vector2f(lastInteracted.getPosition().x, lastInteracted.getPosition().y));
                    state.getHandlerState().setBindedItem(null);
                    return state;
                }
                Slot selectedSlot = activeSlot == null ? otherSlot : activeSlot;
                selectedSlot.setItem(bindedItem);
                bindedItem.getIcon().setPosition(new Vector2f(selectedSlot.getPosition().x, selectedSlot.getPosition().y));
                lastInteracted.setItem(null);
                state.getHandlerState().setBindedItem(null);
            }
        }
        return state;
    }
}
