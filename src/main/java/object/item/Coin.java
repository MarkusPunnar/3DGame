package object.item;

import engine.loader.Loader;

public class Coin extends Item {

    private int amount;
    private final String textureName;

    public Coin(Loader loader, int amount) {
        super(new Icon(loader, "coin"));
        this.amount = amount;
        this.textureName = "coin";
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTextureName() {
        return textureName;
    }
}
