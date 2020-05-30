package game.object.item;

import com.google.common.flogger.FluentLogger;

public abstract class Item {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

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
            logger.atInfo().log("This item is not stackable");
            return false;
        }
        if (!getClass().equals(otherItem.getClass())) {
            logger.atInfo().log("Selected items are incompatible to stack");
            return false;
        }
        this.setAmount(this.getAmount() + otherItem.getAmount());
        logger.atInfo().log("Stacked items successfully");
        return true;
    }
}
