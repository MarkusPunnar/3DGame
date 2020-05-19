package object.item;

import engine.loader.VAOLoader;

public class Coin extends Item {

    private int amount;

    public Coin(VAOLoader loader, int amount) {
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

    @Override
    public float getPaddingX() {
        return 0.01f;
    }

    @Override
    public float getPaddingY() {
        return 0.025f;
    }

}
