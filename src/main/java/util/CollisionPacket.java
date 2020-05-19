package util;

import org.joml.Matrix3f;
import org.joml.Vector3f;
import util.math.MathUtil;

public class CollisionPacket {

    private Vector3f velocityR3;
    private Vector3f positionR3;

    private Vector3f movementVelocity;
    private Vector3f ellipticVelocity;
    private Vector3f normalizedEllipticVelocity;
    private Vector3f basePoint;

    private boolean foundCollision;
    private float nearestDistance;
    private Vector3f intersectionPoint;

    public CollisionPacket(Vector3f velocityR3, Vector3f positionR3) {
        this.velocityR3 = velocityR3;
        this.positionR3 = positionR3;
        Matrix3f ellipticMatrix = MathUtil.getEllipticMatrix();
        ellipticVelocity = new Vector3f();
        velocityR3.mul(ellipticMatrix, ellipticVelocity);
        normalizedEllipticVelocity = new Vector3f();
        ellipticVelocity.normalize(normalizedEllipticVelocity);
        basePoint = new Vector3f();
        positionR3.mul(ellipticMatrix, basePoint);
        foundCollision = false;
        nearestDistance = Float.MAX_VALUE;
        intersectionPoint = new Vector3f();
        movementVelocity = new Vector3f();
    }

    public Vector3f getVelocityR3() {
        return velocityR3;
    }

    public void setVelocityR3(Vector3f velocityR3) {
        this.velocityR3 = velocityR3;
    }

    public Vector3f getPositionR3() {
        return positionR3;
    }

    public void setPositionR3(Vector3f positionR3) {
        this.positionR3 = positionR3;
    }

    public Vector3f getEllipticVelocity() {
        return ellipticVelocity;
    }

    public void setEllipticVelocity(Vector3f ellipticVelocity) {
        this.ellipticVelocity = ellipticVelocity;
    }

    public Vector3f getBasePoint() {
        return basePoint;
    }

    public void setBasePoint(Vector3f basePoint) {
        this.basePoint = basePoint;
    }

    public boolean hasFoundCollision() {
        return foundCollision;
    }

    public void setFoundCollision(boolean foundCollision) {
        this.foundCollision = foundCollision;
    }

    public float getNearestDistance() {
        return nearestDistance;
    }

    public void setNearestDistance(float nearestDistance) {
        this.nearestDistance = nearestDistance;
    }

    public Vector3f getIntersectionPoint() {
        return intersectionPoint;
    }

    public void setIntersectionPoint(Vector3f intersectionPoint) {
        this.intersectionPoint = intersectionPoint;
    }

    public Vector3f getNormalizedEllipticVelocity() {
        return normalizedEllipticVelocity;
    }

    public void setNormalizedEllipticVelocity(Vector3f normalizedEllipticVelocity) {
        this.normalizedEllipticVelocity = normalizedEllipticVelocity;
    }

    public Vector3f getMovementVelocity() {
        return movementVelocity;
    }

    public void setMovementVelocity(Vector3f movementVelocity) {
        this.movementVelocity = movementVelocity;
    }
}
