package object.scene.generation;

import object.RenderObject;
import object.env.Light;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface Generator {

    List<? extends RenderObject> generate(List<Light> lights) throws IOException, URISyntaxException;
}
