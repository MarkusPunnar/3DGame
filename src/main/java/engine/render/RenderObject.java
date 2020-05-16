package engine.render;

import engine.font.GUIText;
import engine.model.Model;
import engine.model.TexturedModel;
import engine.shader.Shader;
import engine.texture.ObjectType;
import org.joml.Vector3f;

public interface RenderObject {

    int getID();

    Model getModel();

    Vector3f getPosition();

    Vector3f getRotation();

    Vector3f getScaleVector();

    default int getPriority() {
        return 0;
    }

    default GUIText getGuiText() {
        return null;
    }

    ObjectType getType();

    void prepareObject(Shader shader);
}
