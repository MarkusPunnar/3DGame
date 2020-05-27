package object.generation;

import object.RenderObject;
import object.env.Light;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface Generator {

    List<? extends RenderObject> generate() throws IOException, URISyntaxException;
}
