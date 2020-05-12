package game.state;

import object.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import util.math.MathUtil;
import util.math.structure.Triangle;
import util.octree.BoundingBox;
import util.octree.OctTree;

import java.util.List;

public class GameState {

    private State currentState;
    private HandlerState handlerState;
    private OctTree currentTree;

    public GameState() {
        this.currentState = State.IN_GAME;
        this.handlerState = new HandlerState();
    }

    public State getCurrentState() {
        return currentState;
    }

    public HandlerState getHandlerState() {
        return handlerState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public OctTree getCurrentTree() {
        return currentTree;
    }

    public void setCurrentTree(OctTree currentTree) {
        this.currentTree = currentTree;
    }
}
