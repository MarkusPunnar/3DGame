package interraction;

import object.item.Slot;
import org.joml.Vector2f;

public interface Lootable {

    Slot[] getContent();

    void initSlot(int textureID, int hoverID, Vector2f position, Vector2f scale, int index);
}
