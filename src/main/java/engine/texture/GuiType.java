package engine.texture;

public enum GuiType {

    INVENTORY("inventory"),
    INVENTORY_TITLE("purple"),
    SLOT("slot"),
    SLOT_HOVER("hoverslot"),
    CHEST("chest");

    private final String textureName;

    public String getTextureName() {
        return textureName;
    }

    GuiType(String textureName) {
        this.textureName = textureName;
    }
}
