package engine.texture;

public class ModelTexture {

    private int textureID;

    private float reflectivity;
    private float shineDamper;
    private float tilingFactor;

    private boolean transparency;
    private boolean useFakeLighting;

    public ModelTexture(int textureID) {
        this.textureID = textureID;
        reflectivity = 0;
        shineDamper = 1;
        tilingFactor = 1;
        transparency = false;
        useFakeLighting = false;
    }

    public ModelTexture(int textureID, float tilingFactor) {
        this.textureID = textureID;
        this.tilingFactor = tilingFactor;
        reflectivity = 0;
        shineDamper = 1;
        transparency = false;
        useFakeLighting = false;
    }

    public boolean useFakeLighting() {
        return useFakeLighting;
    }

    public void setFakeLighting(boolean fakeLighting) {
        this.useFakeLighting = fakeLighting;
    }

    public boolean isTransparent() {
        return transparency;
    }

    public void isTransparent(boolean transparency) {
        this.transparency = transparency;
    }

    public int getTextureID() {
        return textureID;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getTilingFactor() {
        return tilingFactor;
    }

    public void setTilingFactor(float tilingFactor) {
        this.tilingFactor = tilingFactor;
    }
}
