package object.scene.generation;

import engine.render.RenderObject;
import object.Entity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface Generator {

    List<? extends RenderObject> generate() throws IOException, URISyntaxException;
}
