package object.item;

import engine.loader.Loader;

public class Coin extends Item {

    private int amount;

    public Coin(Loader loader, int amount) {
        super(new Icon(loader, "coin"));
        this.amount = amount;
    }

    public boolean isStackable() {
        return true;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

}
