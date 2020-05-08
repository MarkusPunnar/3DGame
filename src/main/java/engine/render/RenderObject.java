package engine.render;

import engine.model.TexturedModel;
import org.joml.Vector3f;

public interface RenderObject {

    int getID();

    TexturedModel getTexturedModel();

    Vector3f getPosition();

    Vector3f getRotation();

    Vector3f getScaleVector();
}
