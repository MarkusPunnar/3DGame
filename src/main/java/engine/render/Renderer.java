package engine.render;

import engine.model.TexturedModel;
import engine.shader.Shader;
import util.OpenGLUtil;

import java.util.Collection;

import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public interface Renderer {

    void render(Collection<? extends RenderObject> objects);

    Shader getShader();

    void bindModel(TexturedModel model);

    default void unbindModel() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        OpenGLUtil.enableCulling();
    }
}
