package renderEngine;

import object.RenderObject;
import model.TexturedModel;

import java.util.Collection;

import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public interface Renderer {

    void render(Collection<RenderObject> objects);

    Shader getShader();

    void bindTexturedModel(TexturedModel model);

    default void unbindModel() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        ParentRenderer.enableCulling();
    }

    void prepareObject(RenderObject object);
}
