package util.math.structure;

import org.joml.Vector3f;

public class Triangle {

    private Vector3f[] vertices;

    public Triangle(Vector3f[] vertices) {
        if (vertices.length != 3) {
            throw new IllegalArgumentException("Invalid vertex count for a triangle");
        }
        this.vertices = vertices;
    }

    public Vector3f[] getVertices() {
        return vertices;
    }
}
