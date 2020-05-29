package object.terrain;

public class TerrainTexturePack {

    private TerrainTexture backgroundTexture;
    private TerrainTexture redTexture;
    private TerrainTexture greenTexture;
    private TerrainTexture blueTexture;

    public TerrainTexturePack(TerrainTexture backgroundTexture, TerrainTexture redTexture, TerrainTexture greenTexture, TerrainTexture blueTexture) {
        this.backgroundTexture = backgroundTexture;
        this.redTexture = redTexture;
        this.greenTexture = greenTexture;
        this.blueTexture = blueTexture;
    }

    public TerrainTexture getBackgroundTexture() {
        return backgroundTexture;
    }

    public TerrainTexture getRedTexture() {
        return redTexture;
    }

    public TerrainTexture getGreenTexture() {
        return greenTexture;
    }

    public TerrainTexture getBlueTexture() {
        return blueTexture;
    }
}
