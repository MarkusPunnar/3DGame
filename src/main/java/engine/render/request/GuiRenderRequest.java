package engine.render.request;

import com.google.common.flogger.FluentLogger;
import engine.DisplayManager;
import engine.font.GUIText;
import game.interraction.Inventory;
import game.interraction.LootableEntity;
import game.interraction.handle.HandlerUtil;
import game.object.RenderObject;
import game.object.generation.GenerationUtil;
import game.object.item.Item;
import game.object.item.Slot;
import game.state.Game;
import game.state.HandlerState;
import game.state.State;
import game.ui.ObjectType;
import game.ui.UIComponent;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.List;

public class GuiRenderRequest extends RenderRequest {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final RenderObject object;
    private final Vector2f position;
    private final Vector2f scale;
    private final String name;
    private final float lifeTime;
    private final GUIText text;


    private GuiRenderRequest(Builder builder) {
        super(builder);
        this.position = builder.position;
        this.scale = builder.scale;
        this.name = builder.name;
        this.lifeTime = builder.lifeTime;
        this.object = builder.object;
        this.text = builder.text;
    }

    public static class Builder extends RenderRequest.Builder {

        private Vector2f position = new Vector2f();
        private Vector2f scale = new Vector2f();
        private  String name = "";
        private float lifeTime = Float.MAX_VALUE;
        private GUIText text = null;
        private RenderObject object = null;

        public Builder(RequestType requestType, ObjectType objectType) {
            super(requestType, objectType);
        }

        public Builder position(Vector2f position) {
            this.position = position;
            return this;
        }

        public Builder scale(Vector2f scale) {
            this.scale = scale;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder lifetime(float time) {
            this.lifeTime = time;
            return this;
        }

        public Builder forObject(RenderObject object) {
            this.object = object;
            return this;
        }

        public Builder withText(GUIText text) {
            this.text = text;
            return this;
        }

        @Override
        public RenderRequest build() {
            return new GuiRenderRequest(this);
        }
    }

    @Override
    protected void handleUpdate() {
        //TODO: Text updating
    }

    @Override
    protected void handleRemove() {
        Game state = Game.getInstance();
        switch (getObjectType()) {
            case INVENTORY:
                Inventory inventory = state.getPlayer().getInventory();
                HandlerUtil.removeGui(List.of(ObjectType.INVENTORY, ObjectType.SLOT));
                renderer.getIcons().clear();
                removeGuiTexts(inventory.getInventorySlots());
                state.setCurrentState(State.IN_GAME);
                inventory.setOpen(false);
                GLFW.glfwSetCursorPos(DisplayManager.getWindow(), DisplayManager.getWidth() / 2f, DisplayManager.getHeight() / 2f);
                logger.atInfo().log("Player inventory GUI removed");
                break;
            case CHEST:
                HandlerUtil.removeGui(List.of(ObjectType.CHEST, ObjectType.SLOT));
                renderer.getIcons().clear();
                removeGuiTexts(HandlerState.getInstance().getLastLooted().getContent());
                state.setCurrentState(State.IN_GAME);
                HandlerState.getInstance().setLastLooted(null);
                GLFW.glfwSetCursorPos(DisplayManager.getWindow(), DisplayManager.getWidth() / 2f, DisplayManager.getHeight() / 2f);
                logger.atInfo().log("Chest interface removed");
                break;
            //TODO: TEXT
        }
    }

    @Override
    protected void handleAdd() throws IOException {
        Game state = Game.getInstance();
        switch (getObjectType()) {
            case INVENTORY:
                state.setCurrentState(State.IN_INVENTORY);
                renderInventory();
                state.getPlayer().getInventory().setOpen(true);
                logger.atInfo().log("Rendered player inventory GUI");
                break;
            case CHEST:
                state.setCurrentState(State.IN_CHEST);
                renderChestInterface();
                logger.atInfo().log("Rendered chest interface");
                break;
            case GUI:
                UIComponent gui = new UIComponent(GenerationUtil.getTextureFromCache(name),
                        position, scale, ObjectType.GUI, lifeTime);
                renderer.getGuis().add(gui);
                if (text != null) {
                    gui.setGuiText(text);
                    renderer.loadText(text);
                }
                break;
            default:
        }
    }

    private void renderInventory() throws IOException {
        Inventory playerInventory = Game.getInstance().getPlayer().getInventory();
        playerInventory.updateSlots(position, scale);
        renderer.getGuis().add(new UIComponent(GenerationUtil.getTextureFromCache("inventory"),
                position, scale, ObjectType.INVENTORY));
        renderItems(playerInventory.getInventorySlots());
    }

    private void renderChestInterface() throws IOException {
        LootableEntity lastLootable = HandlerState.getInstance().getLastLooted();
        lastLootable.updateSlots(position, scale);
        renderer.getGuis().add(new UIComponent(GenerationUtil.getTextureFromCache("inventory"),
                position, scale, ObjectType.CHEST));
        renderChestItems(lastLootable);
    }

    private void renderChestItems(LootableEntity lastLootable) {
        if (lastLootable == null) {
            return;
        }
        renderItems(lastLootable.getContent());
    }

    private void renderItems(Slot[] content) {
        for (Slot slot : content) {
            Item slotItem = slot.getItem();
            if (slotItem != null) {
                GUIText itemText = slotItem.getGuiText();
                Vector3f iconPosition = slotItem.getIcon().getPosition();
                Vector2f textPosition = new Vector2f((1 + iconPosition.x) / 2f + slotItem.getIcon().getScaleVector().x * 0.15f,
                        Math.abs(iconPosition.y - 1) / 2f + slotItem.getIcon().getScaleVector().y * 0.2f);
                if (itemText == null) {
                    itemText = new GUIText.Builder(String.valueOf(slotItem.getAmount())).position(textPosition).fontSize(0.5f).colour(new Vector3f()).build();
                    slotItem.setGuiText(itemText);
                } else {
                    itemText.setPosition(textPosition);
                }
                renderer.processIcon(slotItem.getIcon());
                renderer.loadText(itemText);
            }
        }
    }

    private void removeGuiTexts(Slot[] slots) {
        for (Slot inventorySlot : slots) {
            if (!inventorySlot.isFree() && inventorySlot.getItem().getGuiText() != null) {
                renderer.removeText(inventorySlot.getItem().getGuiText());
            }
        }
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getScale() {
        return scale;
    }
}
