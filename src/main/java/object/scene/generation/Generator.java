package object.scene.generation;

import object.RenderObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface Generator {

    List<? extends RenderObject> generate() throws IOException, URISyntaxException;
}
