package game.state;

import util.octree.OctTree;

public class GameState {

    private static final GameState INSTANCE = new GameState();

    private State currentState;
    private OctTree currentTree;

    public GameState() {
        this.currentState = State.IN_GAME;
    }

    public static GameState getInstance() {
        return INSTANCE;
    }

    public State getCurrentState() {
        return currentState;
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
