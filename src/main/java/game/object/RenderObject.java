package game.object;

import engine.font.GUIText;
import engine.model.Model;
import engine.shader.Shader;
import game.ui.ObjectType;
import org.joml.Vector3f;
import util.math.structure.Triangle;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RenderObject {

    public abstract int getID();

    public abstract Model getModel();

    public abstract Vector3f getPosition();

    public abstract Vector3f getRotation();

    public abstract Vector3f getScaleVector();

    public GUIText getGuiText() {
        return null;
    }

    public List<Triangle> getTriangles() {
        return Collections.emptyList();
    }

    public abstract ObjectType getType();

    public abstract void prepareObject(Shader shader);

    abstract static class Builder {

        public abstract RenderObject build();

        protected abstract Builder self();
    }
}
