package util;

import game.state.Game;
import game.object.RenderObject;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import util.math.MathUtil;
import util.math.structure.Plane3D;
import util.math.structure.Triangle;
import util.octree.BoundingBox;

import java.util.List;
import java.util.Set;

public class CollisionUtil {

    public static float signedDistance(Vector3f basePoint, Plane3D plane) {
        return basePoint.dot(plane.getNormalizedNormal()) + plane.getCoefficients()[3];
    }


    public static void checkCollision(List<RenderObject> renderedObjects, CollisionPacket packet, BoundingBox checkBox) {
        Matrix3f ellipticMatrix = MathUtil.getEllipticMatrix(packet.getHitbox());
        Set<Triangle> closeTriangles = Game.getInstance().getCurrentTree().getCloseTriangles(checkBox);
        for (RenderObject object : renderedObjects) {
            Matrix4f transformationMatrix = MathUtil.createTransformationMatrix(object);
            List<Triangle> objectTriangles = object.getTriangles();
            for (Triangle triangle : objectTriangles) {
                Triangle worldTriangle = new Triangle(CollisionUtil.convertToWorld(transformationMatrix, triangle));
                if (closeTriangles.contains(worldTriangle)) {
                    Vector3f[] verticesElliptic = CollisionUtil.convertToElliptic(ellipticMatrix, worldTriangle);
                    CollisionUtil.checkTriangle(packet, verticesElliptic[0], verticesElliptic[1], verticesElliptic[2]);
                }
            }
        }
    }

    private static void checkTriangle(CollisionPacket packet, Vector3f p1, Vector3f p2, Vector3f p3) {
        Plane3D trianglePlane = new Plane3D(p1, p2, p3);
        //Check only triangles facing the velocity vector
        if (trianglePlane.isFrontFacing(packet.getNormalizedEllipticVelocity())) {
            float t0, t1;
            boolean embeddedInPlane = false;
            float signedDistanceToPlane = signedDistance(packet.getBasePoint(), trianglePlane);
            float normalDotVelocity = trianglePlane.getNormalizedNormal().dot(packet.getEllipticVelocity());
            if (MathUtil.floatEquals(normalDotVelocity, 0.0f)) {
                //Velocity is parallel to plane
                if (Math.abs(signedDistanceToPlane) >= 1.0f) {
                    //No collision possible
                    return;
                } else {
                    //Sphere is embedded in plane
                    embeddedInPlane = true;
                    t0 = 0.0f;
                    t1 = 1.0f;
                }
            } else {
                t0 = (-1.0f - signedDistanceToPlane) / (normalDotVelocity);
                t1 = (1.0f - signedDistanceToPlane) / (normalDotVelocity);
            }
            if (t0 > t1) {
                float temp = t1;
                t1 = t0;
                t0 = temp;
            }
            if (t0 > 1.0f || t1 < 0.0f) {
                //No collision possible
                return;
            }
            //Clamp to [0,1]
            if (t0 < 0.0f) t0 = 0.0f;
            if (t1 < 0.0f) t1 = 0.0f;
            if (t0 > 1.0f) t0 = 1.0f;
            if (t1 > 1.0f) t1 = 1.0f;

            Vector3f collisionPoint = null;
            boolean foundCollision = false;
            float t = 1.0f;
            if (!embeddedInPlane) {
                Vector3f subtracted = new Vector3f();
                Vector3f timeVelocity = new Vector3f();
                Vector3f planeIntersectionPoint = new Vector3f();
                packet.getBasePoint().sub(trianglePlane.getNormalizedNormal(), subtracted);
                packet.getEllipticVelocity().mul(t0, timeVelocity);
                subtracted.add(timeVelocity, planeIntersectionPoint);
                if (isPointInTriangle(planeIntersectionPoint, p1, p2, p3)) {
                    foundCollision = true;
                    t = t0;
                    collisionPoint = planeIntersectionPoint;
                }
            }
            if (!foundCollision) {
                //Check collisions with vertices
                float newT;
                newT = checkCollisionWithVertex(packet, p1, t);
                if (!MathUtil.floatEquals(newT, Float.MAX_VALUE)) {
                    foundCollision = true;
                    t = newT;
                    collisionPoint = p1;
                }
                newT = checkCollisionWithVertex(packet, p2, t);
                if (!MathUtil.floatEquals(newT, Float.MAX_VALUE)) {
                    foundCollision = true;
                    t = newT;
                    collisionPoint = p2;
                }
                newT = checkCollisionWithVertex(packet, p3, t);
                if (!MathUtil.floatEquals(newT, Float.MAX_VALUE)) {
                    foundCollision = true;
                    t = newT;
                    collisionPoint = p3;
                }
                //Check collisions with edges
                // p1 -> p2
                float a, b, c;
                Vector3f edge = new Vector3f();
                Vector3f baseToVertex = new Vector3f();
                Vector3f velocity = packet.getEllipticVelocity();
                p2.sub(p1, edge);
                p1.sub(packet.getBasePoint(), baseToVertex);
                float edgeSquaredLength = edge.lengthSquared();
                float edgeDotVelocity = edge.dot(packet.getEllipticVelocity());
                float edgeDotBaseToVertex = edge.dot(baseToVertex);
                a = edgeSquaredLength * (-velocity.lengthSquared()) + edgeDotVelocity * edgeDotVelocity;
                b = edgeSquaredLength * (2 * velocity.dot(baseToVertex)) - 2 * edgeDotVelocity * edgeDotBaseToVertex;
                c = edgeSquaredLength * (1 - baseToVertex.lengthSquared()) + edgeDotBaseToVertex * edgeDotBaseToVertex;
                newT = MathUtil.solveQuadratic(a, b, c, t);
                if (!MathUtil.floatEquals(newT, Float.MAX_VALUE)) {
                    float f = (edgeDotVelocity * newT - edgeDotBaseToVertex) / edgeSquaredLength;
                    if (f >= 0.0f && f <= 1.0f) {
                        t = newT;
                        foundCollision = true;
                        Vector3f newCollisionPoint = new Vector3f();
                        Vector3f scaledEdge = new Vector3f();
                        edge.mul(f, scaledEdge);
                        p1.add(scaledEdge, newCollisionPoint);
                        collisionPoint = newCollisionPoint;
                    }
                }
                //p2 -> p3
                p3.sub(p2, edge);
                p2.sub(packet.getBasePoint(), baseToVertex);
                edgeSquaredLength = edge.lengthSquared();
                edgeDotVelocity = edge.dot(packet.getEllipticVelocity());
                edgeDotBaseToVertex = edge.dot(baseToVertex);
                a = edgeSquaredLength * (-velocity.lengthSquared()) + edgeDotVelocity * edgeDotVelocity;
                b = edgeSquaredLength * (2 * velocity.dot(baseToVertex)) - 2 * edgeDotVelocity * edgeDotBaseToVertex;
                c = edgeSquaredLength * (1 - baseToVertex.lengthSquared()) + edgeDotBaseToVertex * edgeDotBaseToVertex;
                newT = MathUtil.solveQuadratic(a, b, c, t);
                if (!MathUtil.floatEquals(newT, Float.MAX_VALUE)) {
                    float f = (edgeDotVelocity * newT - edgeDotBaseToVertex) / edgeSquaredLength;
                    if (f >= 0.0f && f <= 1.0f) {
                        t = newT;
                        foundCollision = true;
                        Vector3f newCollisionPoint = new Vector3f();
                        Vector3f scaledEdge = new Vector3f();
                        edge.mul(f, scaledEdge);
                        p2.add(scaledEdge, newCollisionPoint);
                        collisionPoint = newCollisionPoint;
                    }
                }
                //p3 -> p1
                p1.sub(p3, edge);
                p3.sub(packet.getBasePoint(), baseToVertex);
                edgeSquaredLength = edge.lengthSquared();
                edgeDotVelocity = edge.dot(packet.getEllipticVelocity());
                edgeDotBaseToVertex = edge.dot(baseToVertex);
                a = edgeSquaredLength * (-velocity.lengthSquared()) + edgeDotVelocity * edgeDotVelocity;
                b = edgeSquaredLength * (2 * velocity.dot(baseToVertex)) - 2 * edgeDotVelocity * edgeDotBaseToVertex;
                c = edgeSquaredLength * (1 - baseToVertex.lengthSquared()) + edgeDotBaseToVertex * edgeDotBaseToVertex;
                newT = MathUtil.solveQuadratic(a, b, c, t);
                if (!MathUtil.floatEquals(newT, Float.MAX_VALUE)) {
                    float f = (edgeDotVelocity * newT - edgeDotBaseToVertex) / edgeSquaredLength;
                    if (f >= 0.0f && f <= 1.0f) {
                        t = newT;
                        foundCollision = true;
                        Vector3f newCollisionPoint = new Vector3f();
                        Vector3f scaledEdge = new Vector3f();
                        edge.mul(f, scaledEdge);
                        p3.add(scaledEdge, newCollisionPoint);
                        collisionPoint = newCollisionPoint;
                    }
                }
            }
            if (foundCollision) {
                float distToCollision = packet.getEllipticVelocity().length() * t;
                if (!packet.hasFoundCollision() || packet.getNearestDistance() > distToCollision) {
                    packet.setNearestDistance(distToCollision);
                    packet.setIntersectionPoint(collisionPoint);
                    packet.setFoundCollision(true);
                }
            }
        }
    }

    private static float checkCollisionWithVertex(CollisionPacket packet, Vector3f vertex, float currentCollisionTime) {
        float a, b, c;
        a = packet.getEllipticVelocity().lengthSquared();
        Vector3f vectorFromVertex = new Vector3f();
        packet.getBasePoint().sub(vertex, vectorFromVertex);
        b = packet.getEllipticVelocity().dot(vectorFromVertex) * 2;
        Vector3f vectorToVertex = new Vector3f();
        vertex.sub(packet.getBasePoint(), vectorToVertex);
        c = vectorToVertex.lengthSquared() - 1;
        return MathUtil.solveQuadratic(a, b, c, currentCollisionTime);
    }

    private static boolean isPointInTriangle(Vector3f planeIntersectionPoint, Vector3f p1, Vector3f p2, Vector3f p3) {
        Vector3f firstTriangleEdge = new Vector3f();
        p2.sub(p1, firstTriangleEdge);
        Vector3f secondTriangleEdge = new Vector3f();
        p3.sub(p1, secondTriangleEdge);
        Vector3f normal = new Vector3f();
        firstTriangleEdge.cross(secondTriangleEdge, normal);
        Vector3f vectorToIntersection = new Vector3f();
        planeIntersectionPoint.sub(p1, vectorToIntersection);
        float gamma = firstTriangleEdge.cross(vectorToIntersection).dot(normal) / normal.dot(normal);
        float beta  = vectorToIntersection.cross(secondTriangleEdge).dot(normal) / normal.dot(normal);
        float alpha = 1 - gamma - beta;
        return alpha >= 0 && alpha <= 1 && beta >= 0 && beta <= 1 && gamma >= 0 && gamma <= 1;
    }

    public static Vector3f[] convertToElliptic(Matrix3f ellipticMatrix, Triangle triangle) {
        Vector3f[] verticesElliptic = new Vector3f[3];
        for (int i = 0; i < triangle.getVertices().length; i++) {
            verticesElliptic[i] = triangle.getVertices()[i].mul(ellipticMatrix, new Vector3f());
        }
        return verticesElliptic;
    }

    public static Vector3f[] convertToWorld(Matrix4f transformationMatrix, Triangle triangle) {
        Vector3f[] worldVertices = new Vector3f[3];
        for (int i = 0; i < triangle.getVertices().length; i++) {
            worldVertices[i] = MathUtil.getCoordinates(new Vector4f(triangle.getVertices()[i], 1.0f).mul(transformationMatrix, new Vector4f()));
        }
        return worldVertices;
    }
}
