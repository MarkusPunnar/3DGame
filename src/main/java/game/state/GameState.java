package game.state;

public class GameState {

    private State currentState;
    private HandlerState handlerState;

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
}
