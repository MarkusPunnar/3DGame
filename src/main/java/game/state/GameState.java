package game.state;

import com.google.common.flogger.FluentLogger;
import util.octree.OctTree;

public class GameState {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

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
        logger.atInfo().log("Set current game state to %s", currentState);
        this.currentState = currentState;
    }

    public OctTree getCurrentTree() {
        return currentTree;
    }

    public void setCurrentTree(OctTree currentTree) {
        this.currentTree = currentTree;
    }
}
