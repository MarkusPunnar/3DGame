package engine.texture;

public enum GuiType {

    INVENTORY("inventory"),
    INVENTORY_TITLE("invtitle"),
    CHEST_TITLE("chesttitle"),
    CHEST("chest"),
    SLOT("slot"),
    SLOT_HOVER("hoverslot"),
    ICON("");


    private final String textureName;

    public String getTextureName() {
        return textureName;
    }

    GuiType(String textureName) {
        this.textureName = textureName;
    }
}
