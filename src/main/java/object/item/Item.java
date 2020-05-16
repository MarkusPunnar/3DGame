package object.item;

import engine.font.GUIText;
import org.joml.Vector2f;

public abstract class Item {

    private Icon icon;

    public Item(Icon itemIcon) {
        this.icon = itemIcon;
    }

    public Icon getIcon() {
        return icon;
    }

    public abstract boolean isStackable();

    public int getAmount() {
        return 1;
    }

    public abstract void setAmount(int amount);

    public abstract float getPaddingX();

    public abstract float getPaddingY();

    public boolean stack(Item otherItem) {
        if (!isStackable()) {
            return false;
        }
        if (!getClass().equals(otherItem.getClass())) {
            return false;
        }
        this.setAmount(this.getAmount() + otherItem.getAmount());
        return true;
    }
}
