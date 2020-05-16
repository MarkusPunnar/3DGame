package engine.render;

import engine.model.Model;
import org.joml.Matrix4f;
import engine.shader.StaticShader;

import java.util.Collection;

import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;


public class EntityRenderer implements Renderer {

    private StaticShader shader;
    private Model currentTexture;

    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.doLoadMatrix(projectionMatrix, "projectionMatrix");
        shader.stop();
    }

    public void render(Collection<? extends RenderObject> entities) {
        for (RenderObject entity : entities) {
            Model model = entity.getModel();
            checkCurrentBind(model);
            entity.prepareObject(shader);
            glDrawElements(GL_TRIANGLES, model.getVertexCount(), GL_UNSIGNED_INT, 0);
        }
        unbindModel();
        currentTexture = null;
    }

    private void checkCurrentBind(Model texturedModel) {
        if (currentTexture == null || !currentTexture.equals(texturedModel)) {
            unbindModel();
            bindModel(texturedModel);
            currentTexture = texturedModel;
        }
    }

    public void bindModel(Model model) {
        glBindVertexArray(model.getModelID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        model.prepareShader(shader);
    }

    public StaticShader getShader() {
        return shader;
    }
}
