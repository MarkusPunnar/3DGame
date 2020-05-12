package util.octree;

import engine.render.RenderObject;
import object.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import util.math.MathUtil;
import util.math.structure.Triangle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OctTree {

    private static int test = 0;

    private BoundingBox dimensions;
    private List<Triangle> triangles;
    private OctTree[] children;

    public OctTree(BoundingBox dimensions) {
        this.dimensions = dimensions;
        this.triangles = new ArrayList<>();
        this.children = new OctTree[8];
        buildTree();
    }

    private void buildTree() {
        if (dimensions.getXDistance() <= 2 || dimensions.getYDistance() <= 2 || dimensions.getZDistance() <= 2) {
            return;
        }
        float halfX = dimensions.getXMidpoint();
        float halfY = dimensions.getYMidpoint();
        float halfZ = dimensions.getZMidpoint();
        float x1 = dimensions.getFirst().x;
        float y1 = dimensions.getFirst().y;
        float z1 = dimensions.getFirst().z;
        float x2 = dimensions.getSecond().x;
        float y2 = dimensions.getSecond().y;
        float z2 = dimensions.getSecond().z;
        children[0] = new OctTree(new BoundingBox(dimensions.getFirst(), new Vector3f(halfX, halfY, halfZ)));
        children[1] = new OctTree(new BoundingBox(new Vector3f(halfX, y1, z1), new Vector3f(x2, halfY, halfZ)));
        children[2] = new OctTree(new BoundingBox(new Vector3f(x1, y1, halfZ), new Vector3f(halfX, halfY, z2)));
        children[3] = new OctTree(new BoundingBox(new Vector3f(halfX, y1, halfZ), new Vector3f(x2, halfY, z2)));
        children[4] = new OctTree(new BoundingBox(new Vector3f(x1, halfY, z1), new Vector3f(halfX, y2, halfZ)));
        children[5] = new OctTree(new BoundingBox(new Vector3f(halfX, halfY, z1), new Vector3f(x2, y2, halfZ)));
        children[6] = new OctTree(new BoundingBox(new Vector3f(x1, halfY, halfZ), new Vector3f(halfX, y2, z2)));
        children[7] = new OctTree(new BoundingBox(new Vector3f(halfX, halfY, halfZ), dimensions.getSecond()));
    }


    public void initTree(List<RenderObject> objects) {
        for (RenderObject entity : objects) {
            Matrix4f transformationMatrix = MathUtil.createTransformationMatrix(entity);
            for (Triangle triangle : entity.getTexturedModel().getRawModel().getTriangles()) {
                Vector3f[] worldVertices = new Vector3f[3];
                for (int i = 0; i < triangle.getVertices().length; i++) {
                    worldVertices[i] =  MathUtil.getCoordinates(new Vector4f(triangle.getVertices()[i], 1.0f).mul(transformationMatrix));
                }
                Triangle worldTriangle = new Triangle(worldVertices);
                worldTriangle.setTriangleBox();
                addTriangle(worldTriangle);
            }
        }
    }

    public Set<Triangle> getCloseTriangles(BoundingBox box) {
        Set<Triangle> closeTriangles = new HashSet<>(triangles);
        for (OctTree child : children) {
            if (child != null && box.intersectsWith(child.getDimensions())) {
                closeTriangles.addAll(child.getCloseTriangles(box));
            }
        }
        return closeTriangles;
    }

    private void addTriangle(Triangle triangle) {
        boolean isInChild = false;
        for (OctTree child : children) {
            if (child != null && triangle.getBoxAroundTriangle().isEmbeddedIn(child.getDimensions())) {
                child.addTriangle(triangle);
                isInChild = true;
            }
        }
        if (!isInChild) {
            triangles.add(triangle);
        }
    }

    public BoundingBox getDimensions() {
        return dimensions;
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    public OctTree[] getChildren() {
        return children;
    }
}