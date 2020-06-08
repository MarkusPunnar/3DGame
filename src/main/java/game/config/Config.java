package game.config;

public class Config {

    private static final Config INSTANCE = new Config();

    private ShadowLevel shadowLevel = ShadowLevel.HIGH;
    private int invertedMouse = 1;

    private Config() {
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    public ShadowLevel getShadowLevel() {
        return shadowLevel;
    }

    public void changeShadowLevel(int change) {
        shadowLevel = ShadowLevel.getById(shadowLevel.ordinal() + change);
    }

    public int getInvertedMouse() {
        return invertedMouse;
    }

    public void setInvertedMouse(int invertedMouse) {
        this.invertedMouse = invertedMouse;
    }
}
