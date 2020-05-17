package interraction;

import engine.model.TexturedModel;
import object.item.Slot;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class LootableEntity extends InteractableEntity {

    public LootableEntity(TexturedModel texturedModel, Vector3f position, Vector3f rotation, Vector3f scaleVector) {
        super(texturedModel, position, rotation, scaleVector);
    }

    public abstract Slot[] getContent();

    public abstract Slot initSlot(int textureID, int hoverID, Vector2f position, Vector2f scale, int index);
}
