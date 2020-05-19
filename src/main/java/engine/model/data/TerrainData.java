package engine.model.data;

public class TerrainData {

    private final float[][] heights;
    private final float[] normals;

    public TerrainData(float[][] heights, float[] normals) {
        this.heights = heights;
        this.normals = normals;
    }

    public float[][] getHeights() {
        return heights;
    }

    public float[] getNormals() {
        return normals;
    }
}
