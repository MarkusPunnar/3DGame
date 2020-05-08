package object.item;

public class Item {

    protected Icon icon;

    public Item(Icon itemIcon) {
        this.icon = itemIcon;
    }

    public Item() {}

    public Icon getIcon() {
        return icon;
    }
}
