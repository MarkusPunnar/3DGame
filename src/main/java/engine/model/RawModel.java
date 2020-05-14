package engine.model;

import org.joml.Vector3f;
import util.math.structure.Triangle;
import util.octree.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class RawModel {

    private int vaoID;
    private int vertexCount;
    private BoundingBox boundingBox;
    private List<Triangle> triangles;

    public RawModel(int vaoID, int vertexCount, BoundingBox box) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.boundingBox = box;
        this.triangles = createTrianglesFromBox();
    }

    private List<Triangle> createTrianglesFromBox() {
        List<Triangle> triangles = new ArrayList<>();
        Vector3f[] vertices = createVerticesFromBox();
        triangles.add(new Triangle(vertices[6], vertices[0], vertices[3]));
        triangles.add(new Triangle(vertices[4], vertices[0], vertices[6]));
        triangles.add(new Triangle(vertices[7], vertices[4], vertices[6]));
        triangles.add(new Triangle(vertices[5], vertices[4], vertices[7]));
        triangles.add(new Triangle(vertices[2], vertices[5], vertices[7]));
        triangles.add(new Triangle(vertices[1], vertices[5], vertices[2]));
        triangles.add(new Triangle(vertices[3], vertices[1], vertices[2]));
        triangles.add(new Triangle(vertices[0], vertices[1], vertices[3]));
        triangles.add(new Triangle(vertices[5], vertices[0], vertices[1]));
        triangles.add(new Triangle(vertices[4], vertices[0], vertices[5]));
        triangles.add(new Triangle(vertices[7], vertices[3], vertices[2]));
        triangles.add(new Triangle(vertices[6], vertices[3], vertices[7]));
        return triangles;
    }

    private Vector3f[] createVerticesFromBox() {
        Vector3f[] vertices = new Vector3f[8];
        if (boundingBox == null) {
            return vertices;
        }
        Vector3f lowerDiag = boundingBox.getFirst();
        Vector3f higherDiag = boundingBox.getSecond();
        vertices[0] = new Vector3f(lowerDiag);
        vertices[1] = new Vector3f(lowerDiag.x, lowerDiag.y, higherDiag.z);
        vertices[2] = new Vector3f(lowerDiag.x, higherDiag.y, higherDiag.z);
        vertices[3] = new Vector3f(lowerDiag.x, higherDiag.y, lowerDiag.z);
        vertices[4] = new Vector3f(higherDiag.x, lowerDiag.y, lowerDiag.z);
        vertices[5] = new Vector3f(higherDiag.x, lowerDiag.y, higherDiag.z);
        vertices[6] = new Vector3f(higherDiag.x, higherDiag.y, lowerDiag.z);
        vertices[7] = new Vector3f(higherDiag);
        return vertices;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }
}