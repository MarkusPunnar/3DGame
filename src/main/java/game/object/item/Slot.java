package game.object.item;

import game.ui.UIComponent;
import game.ui.ObjectType;
import org.joml.Vector2f;

public class Slot extends UIComponent {

    private Item item;
    private int normalTextureID;
    private int hoverTextureID;

    public Slot(int textureID, Vector2f position, Vector2f scale, int hoverTextureID) {
        super(textureID, position, scale, ObjectType.SLOT);
        this.normalTextureID = textureID;
        this.hoverTextureID = hoverTextureID;
    }

    public Slot() {
        super(0, null, null, ObjectType.SLOT);
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
