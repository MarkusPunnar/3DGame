package interraction;

import engine.model.TexturedModel;
import engine.render.RenderObject;
import game.state.GameState;
import object.Entity;
import org.joml.Vector3f;
import util.octree.OctTree;

public abstract class InteractableEntity extends Entity {

    public InteractableEntity(TexturedModel texturedModel, Vector3f position, Vector3f rotation, Vector3f scaleVector) {
        super(texturedModel, position, rotation, scaleVector);
    }

    public abstract void interact();

    public abstract void handleGui(GameState state);

    public abstract float getInteractionTime();

    public abstract void setInteractionTime(float time);
}
