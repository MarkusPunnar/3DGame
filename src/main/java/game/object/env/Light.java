package game.object.env;

import engine.shadow.ShadowFrameBuffer;
import game.object.Player;
import game.state.Game;
import org.joml.Vector3f;
import util.octree.BoundingBox;

public class Light implements Comparable<Light> {

    private Vector3f position;
    private Vector3f colour;
    private Vector3f attenuation;
    private ShadowFrameBuffer fbo;
    private BoundingBox activeRegion;
    private boolean isPointLight;
    private boolean isInitialized;
    private int callCount;

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

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public ShadowFrameBuffer getFbo() {
        return fbo;
    }

    public boolean isPointLight() {
        return isPointLight;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public boolean isActive(Player player) {
        if (callCount < 2) {
            callCount++;
            return true;
        }
        Vector3f playerPos = player.getPosition();
        BoundingBox playerBox = new BoundingBox(
                new Vector3f(playerPos.x - 1, playerPos.y - 1, playerPos.z - 1),
                new Vector3f(playerPos.x + 1, playerPos.y + 1, playerPos.z + 1));
        return !isInitialized || activeRegion == null || playerBox.isEmbeddedIn(activeRegion)
                || playerBox.intersectsWith(activeRegion);
    }


    @Override
    public int compareTo(Light other) {
        if (!isPointLight) {
            return -1;
        }
        if (!other.isPointLight()) {
            return 1;
        }
        Vector3f playerPos = Game.getInstance().getPlayer().getPosition();
        return (int) (playerPos.distanceSquared(position) - playerPos.distanceSquared(other.getPosition()));
    }
}
