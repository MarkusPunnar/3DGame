package object.env;

import engine.shadow.ShadowFrameBuffer;
import org.joml.Vector3f;
import util.octree.BoundingBox;

public class Light {

    private Vector3f position;
    private Vector3f colour;
    private Vector3f attenuation;
    private ShadowFrameBuffer fbo;
    private boolean isPointLight;
    private BoundingBox activeRegion;

    public Light(Vector3f position, Vector3f colour, boolean isPointLight, BoundingBox box) {
        this(position, colour, new Vector3f(1, 0, 0), isPointLight, box);
    }

    public Light(Vector3f position, Vector3f colour, Vector3f attenuation, boolean isPointLight, BoundingBox box) {
        this.position = position;
        this.colour = colour;
        this.attenuation = attenuation;
        this.isPointLight = isPointLight;
        this.fbo = isPointLight ? new ShadowFrameBuffer(true) : new ShadowFrameBuffer(false);
        this.activeRegion = box;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Vector3f attenuation) {
        this.attenuation = attenuation;
    }

    public ShadowFrameBuffer getFbo() {
        return fbo;
    }

    public boolean isPointLight() {
        return isPointLight;
    }

    public BoundingBox getActiveRegion() {
        return activeRegion;
    }
}
