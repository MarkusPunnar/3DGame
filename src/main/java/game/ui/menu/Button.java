package game.ui.menu;

import game.ui.ObjectType;
import game.ui.UIComponent;
import org.joml.Vector2f;

import java.util.concurrent.Callable;

public class Button extends UIComponent {

    private Callable<Void> onClickCallback;

    public Button(int textureID, Vector2f position, Vector2f scale) {
        super(textureID, position, scale, ObjectType.BUTTON);
    }

    public void onClick() throws Exception {
        onClickCallback.call();
    }

    public void setClickCallback(Callable<Void> r) {
        this.onClickCallback = r;
    }

    public Callable<Void> getClickCallback() {
        return onClickCallback;
    }

    public boolean contains(Vector2f mouseCoords) {
        boolean xMatch = Math.abs(getPosition().x - mouseCoords.x) < getScaleVector().x;
        boolean yMatch = Math.abs(getPosition().y - mouseCoords.y) < getScaleVector().y;
        return xMatch && yMatch;
    }
}
