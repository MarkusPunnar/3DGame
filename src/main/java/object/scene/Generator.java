package object.scene;

import object.Entity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface Generator {

    List<Entity> generate() throws IOException, URISyntaxException;
}
