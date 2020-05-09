package object.item;

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

    public boolean stack(Item otherItem) {
        if (!isStackable()) {
            return false;
        }
        this.setAmount(this.getAmount() + otherItem.getAmount());
        return true;
    }
}
