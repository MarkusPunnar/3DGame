package interraction.handle;

import game.state.GameState;

public interface Handler {

    GameState handle(GameState state);
}
