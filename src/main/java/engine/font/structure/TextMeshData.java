package engine.font.structure;

public class TextMeshData {

    private float[] vertices;
    private float[] textureCoords;

    public TextMeshData(float[] vertices, float[] textureCoords) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public int getVertexCount() {
        return vertices.length / 2;
    }
}
