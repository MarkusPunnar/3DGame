package object.item;

import engine.texture.GuiTexture;
import engine.texture.GuiType;
import org.joml.Vector2f;

public class Slot extends GuiTexture {

    private Item item;
    private int normalTextureID;
    private int hoverTextureID;

    public Slot(int textureID, Vector2f position, Vector2f scale, int hoverTextureID) {
        super(textureID, position, scale, GuiType.SLOT);
        this.normalTextureID = textureID;
        this.hoverTextureID = hoverTextureID;
    }

    public Slot() {
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

    public void changeTexture() {
        this.setID(hoverTextureID);
    }

    public void resetTexture() {
        this.setID(normalTextureID);
    }

    public int getHoverTextureID() {
        return hoverTextureID;
    }

    public void setHoverTextureID(int hoverTextureID) {
        this.hoverTextureID = hoverTextureID;
    }

    public int getNormalTextureID() {
        return normalTextureID;
    }

    public void setNormalTextureID(int normalTextureID) {
        this.normalTextureID = normalTextureID;
    }
}
