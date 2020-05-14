package engine.render;

import engine.model.TexturedModel;
import org.joml.Vector3f;

public abstract class RenderObject {

    public abstract int getID();

    public abstract TexturedModel getTexturedModel();

    public abstract Vector3f getPosition();

    public abstract Vector3f getRotation();

    public abstract Vector3f getScaleVector();
}
