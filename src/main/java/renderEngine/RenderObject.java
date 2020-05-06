package renderEngine;

import model.TexturedModel;
import org.joml.Vector3f;

public interface RenderObject {

    int getID();

    TexturedModel getTexturedModel();

    Vector3f getPosition();

    float getRotationX();

    float getRotationY();

    float getRotationZ();

    Vector3f getScaleVector();
}
