package game.interraction.handle;

import com.google.common.flogger.FluentLogger;
import engine.font.GUIText;
import engine.render.ParentRenderer;
import engine.render.request.RenderRequest;
import game.object.Player;
import game.state.Game;
import game.state.HandlerState;

import java.io.IOException;
import java.util.Queue;

public class RenderRequestHandler implements Handler {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    @Override
    public void handle() throws IOException {
        Queue<RenderRequest> requests = HandlerState.getInstance().getRequests();
        while (requests.peek() != null) {
            RenderRequest request = requests.poll();
            logger.atInfo().log("Resolving render request of type %s", request.toString());
            request.handle();
        }
    }
}
