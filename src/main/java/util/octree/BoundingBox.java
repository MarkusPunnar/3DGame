package util.octree;

import org.joml.Vector3f;

public class BoundingBox {

    private Vector3f first;
    private Vector3f second;

    public BoundingBox(Vector3f first, Vector3f second) {
        this.first = first;
        this.second = second;
    }

    public BoundingBox() {}

    public boolean isEmbeddedIn(BoundingBox other) {
        boolean xMatch = this.first.x >= other.first.x && this.second.x <= other.second.x;
        if (xMatch) {
            boolean yMatch = this.first.y >= other.first.y && this.second.y <= other.second.y;
            if (yMatch) {
                return this.first.z >= other.first.z && this.second.z <= other.second.z;
            }
        }
        return false;
    }

    public boolean intersectsWith(BoundingBox other) {
        boolean xMatch = this.second.x >= other.first.x && this.first.x <= other.second.x;
        if (xMatch) {
            boolean yMatch = this.second.y >= other.first.y && this.first.y <= other.second.y;
            if (yMatch) {
                return this.second.z >= other.first.z && this.first.z <= other.second.z;
            }
        }
        return false;
    }

    public Vector3f getFirst() {
        return first;
    }

    public Vector3f getSecond() {
        return second;
    }

    public void setFirst(Vector3f first) {
        this.first = first;
    }

    public void setSecond(Vector3f second) {
        this.second = second;
    }

    public float getXDistance() {
        return Math.abs(second.x - first.x);
    }

    public float getYDistance() {
        return Math.abs(second.y - first.y);
    }

    public float getZDistance() {
        return Math.abs(second.z - first.z);
    }

    public float getXMidpoint() {
        return (first.x + second.x) / 2f;
    }

    public float getYMidpoint() {
        return (first.y + second.y) / 2f;
    }

    public float getZMidpoint() {
        return (first.z + second.z) / 2f;
    }
}
