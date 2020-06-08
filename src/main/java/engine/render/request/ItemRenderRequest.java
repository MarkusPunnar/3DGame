package engine.render.request;

import com.google.common.flogger.FluentLogger;
import engine.font.GUIText;
import game.object.RenderObject;
import game.ui.ObjectType;

public class ItemRenderRequest extends RenderRequest {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private RenderObject object;
    private GUIText guiText;

    public ItemRenderRequest(RequestType requestType, ObjectType objectType, RenderObject object, GUIText text) {
        super(requestType, objectType);
        this.object = object;
        this.guiText = text;
    }


    public RenderObject getObject() {
        return object;
    }

    @Override
    protected void handleUpdate() {
        switch (getObjectType()) {
            case TEXT:
                renderer.loadText(guiText);
                logger.atInfo().log("Refreshed GUI text");
                break;
        }
    }

    @Override
    protected void handleRemove() {
        switch (getObjectType()) {
            case ICON:
                renderer.getIcons().remove(object);
                if (guiText != null) {
                    renderer.removeText(guiText);
                }
                logger.atInfo().log("Removed item GUI of type %s", object.getClass().getSimpleName());
                break;
            case TEXT:
                renderer.removeText(guiText);
                logger.atInfo().log("Removed GUI text");
                break;
            default:
        }
    }

    @Override
    protected void handleAdd() {
        switch (getObjectType()) {
            case TEXT:
                renderer.loadText(guiText);
                break;
            default:
        }
    }
}
