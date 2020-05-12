package util.math.structure;

import org.joml.Vector3f;
import util.octree.BoundingBox;

import java.util.Arrays;

public class Triangle {

    private Vector3f[] vertices;
    private BoundingBox boxAroundTriangle;

    public Triangle(Vector3f[] vertices) {
        if (vertices.length != 3) {
            throw new IllegalArgumentException("Invalid vertex count for a triangle");
        }
        this.vertices = vertices;
    }

    public Vector3f[] getVertices() {
        return vertices;
    }

    public BoundingBox getBoxAroundTriangle() {
        return boxAroundTriangle;
    }

    public void setTriangleBox() {
        this.boxAroundTriangle = new BoundingBox(new Vector3f(Math.min(vertices[0].x, Math.min(vertices[1].x, vertices[2].x)),
                Math.min(vertices[0].y, Math.min(vertices[1].y, vertices[2].y)), Math.min(vertices[0].z, Math.min(vertices[1].z, vertices[2].z))),
                new Vector3f(Math.max(vertices[0].x, Math.max(vertices[1].x, vertices[2].x)), Math.max(vertices[0].y, Math.max(vertices[1].y, vertices[2].y)),
                        Math.max(vertices[0].z, Math.max(vertices[1].z, vertices[2].z))));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Triangle triangle = (Triangle) other;
        return Arrays.equals(vertices, triangle.vertices);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vertices);
    }
}
