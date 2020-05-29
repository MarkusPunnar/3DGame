package game.ui;

public enum ObjectType {

    INVENTORY("inventory"),
    INVENTORY_TITLE("invtitle"),
    CHEST_TITLE("chesttitle"),
    CHEST("chest"),
    SLOT("slot"),
    SLOT_HOVER("hoverslot"),
    ICON(""),
    BACKGROUND_IMAGE(""),
    BUTTON(""),

    ENTITY(""),
    TERRAIN(""),
    TEXT("");


    private final String textureName;

    public String getTextureName() {
        return textureName;
    }

    ObjectType(String textureName) {
        this.textureName = textureName;
    }
}
