package object.item;

import engine.texture.GuiTexture;
import engine.texture.GuiType;
import org.joml.Vector2f;

public class Slot extends GuiTexture {

    private Item item;
    private int normalTextureID;
    private int hoverTextureID;

    public Slot(int textureID, Vector2f position, Vector2f scale, GuiType type, int hoverTextureID) {
        super(textureID, position, scale, type);
        this.normalTextureID = textureID;
        this.hoverTextureID = hoverTextureID;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean isFree() {
        return item == null;
    }
}
