package engine.render;

import engine.font.GUIText;
import engine.model.Model;
import engine.model.TexturedModel;
import engine.shader.Shader;
import engine.texture.ObjectType;
import org.joml.Vector3f;

public abstract class RenderObject {

    public abstract int getID();

    public abstract Model getModel();

    public abstract Vector3f getPosition();

    public abstract Vector3f getRotation();

    public abstract Vector3f getScaleVector();

    public int getPriority() {
        return 0;
    }

    public GUIText getGuiText() {
        return null;
    }

    public abstract ObjectType getType();

    public abstract void prepareObject(Shader shader);
}
