package object.scene;

import interraction.Interactable;
import model.TexturedModel;
import object.Entity;
import object.item.Item;
import org.joml.Vector3f;

public class Chest extends Entity implements Interactable {

    private boolean isOpened;
    private float sinceLastInteraction;
    private TexturedModel openModel;
    private TexturedModel closedModel;
    private Item[] content;

    public Chest(TexturedModel texturedModel, Vector3f position, float rotationX, float rotationY, float rotationZ, Vector3f scaleVector, TexturedModel openModel, TexturedModel closedModel) {
        super(texturedModel, position, rotationX, rotationY, rotationZ, scaleVector);
        this.isOpened = false;
        this.openModel = openModel;
        this.closedModel = closedModel;
        this.sinceLastInteraction = Float.MAX_VALUE;
        this.content = new Item[5];
    }

    @Override
    public void interact() {
        if (sinceLastInteraction > 0.5f) {
            if (!isOpened) {
                setTexturedModel(openModel);
                isOpened = true;
                sinceLastInteraction = 0;
            } else {
                setTexturedModel(closedModel);
                isOpened = false;
                sinceLastInteraction = 0;
            }
        }
    }

    @Override
    public void openGui() {

    }

    @Override
    public float getInteractionTime() {
        return sinceLastInteraction;
    }

    @Override
    public void setInteractionTime(float time) {
        this.sinceLastInteraction = time;
    }
}
