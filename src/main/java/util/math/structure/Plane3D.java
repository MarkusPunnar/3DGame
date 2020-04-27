package util.math.structure;

import org.joml.Vector3f;

public class Plane3D {

    private Vector3f normalizedNormal;
    private Vector3f origin;
    private float[] coefficients;

    public Plane3D(Vector3f normalizedNormal, Vector3f origin) {
        this.normalizedNormal = normalizedNormal.normalize();
        this.origin = origin;
        calculateCoefficients();
    }

    public Plane3D(Vector3f p1, Vector3f p2, Vector3f p3) {
        Vector3f firstEdgeVector = new Vector3f();
        Vector3f secondEdgeVector = new Vector3f();
        p2.sub(p1, firstEdgeVector);
        p3.sub(p1, secondEdgeVector);
        Vector3f normal = firstEdgeVector.cross(secondEdgeVector);
        normal.normalize();
        this.normalizedNormal = normal;
        this.origin = p1;
        calculateCoefficients();
    }

    public Vector3f getNormalizedNormal() {
        return normalizedNormal;
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public float[] getCoefficients() {
        return coefficients;
    }

    private void calculateCoefficients() {
        coefficients = new float[4];
        coefficients[0] = normalizedNormal.x;
        coefficients[1] = normalizedNormal.y;
        coefficients[2] = normalizedNormal.z;
        coefficients[3] = -(normalizedNormal.x * origin.x + normalizedNormal.y * origin.y + normalizedNormal.z * origin.z);
    }

    public boolean isFrontFacing(Vector3f direction) {
        float dot = normalizedNormal.dot(direction);
        return dot <= Math.pow(10, -2);
    }
}
