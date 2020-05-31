package game.object.generation;

import game.object.RenderObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface Generator {

    List<? extends RenderObject> generate() throws IOException;
}
