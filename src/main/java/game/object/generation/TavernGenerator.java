package game.object.generation;

import engine.model.ModelTexture;
import engine.model.TexturedModel;
import game.object.Player;
import game.state.Game;
import org.joml.Vector3f;

import java.io.IOException;

public class TavernGenerator {

    public Player generatePlayer() throws IOException {
        ModelTexture purpleTexture = new ModelTexture(Game.getInstance().getLoader().loadObjectTexture("purple"));
        TexturedModel playerModel = EntityLoader.getTexturedModel("player", purpleTexture);
        return new Player(playerModel, new Vector3f(-240, 9, 64), new Vector3f(), new Vector3f(3));
    }
}
