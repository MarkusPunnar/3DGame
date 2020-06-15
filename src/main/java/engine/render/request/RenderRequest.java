package engine.render.request;

import engine.render.ParentRenderer;
import game.state.Game;
import game.ui.ObjectType;

import java.io.IOException;

public abstract class RenderRequest {

    private final RequestType requestType;
    private final ObjectType objectType;

    protected ParentRenderer renderer;

    protected RenderRequest(Builder builder) {
        this.requestType = builder.requestType;
        this.objectType = builder.objectType;
        this.renderer = Game.getInstance().getRenderer();
    }

    public void handle() throws IOException {
        switch (getRequestType()) {
            case ADD:
                handleAdd();
                break;
            case REMOVE:
                handleRemove();
                break;
            case UPDATE:
                handleUpdate();
                break;
            default:
        }
    }

    protected abstract void handleUpdate();

    protected abstract void handleRemove();

    protected abstract void handleAdd() throws IOException;

    abstract static class Builder {

        private final RequestType requestType;
        private final ObjectType objectType;

        public Builder(RequestType requestType, ObjectType objectType) {
            this.requestType = requestType;
            this.objectType = objectType;
        }

        public abstract RenderRequest build();
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    @Override
    public String toString() {
        return requestType.toString();
    }
}
