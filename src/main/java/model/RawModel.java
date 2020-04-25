package model;

import util.math.structure.Triangle;

import java.util.List;

public class RawModel {

    private int vaoID;
    private int vertexCount;
    private List<Triangle> triangles;

    public RawModel(int vaoID, int vertexCount, List<Triangle> triangles) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.triangles = triangles;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }
}