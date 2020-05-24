package engine.render;

import engine.model.Model;
import engine.shader.Shader;
import object.RenderObject;
import org.joml.Matrix4f;
import engine.shader.EntityShader;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;


public class EntityRenderer implements Renderer {

    private Shader shader;
    private Model currentTexture;

    public EntityRenderer(Matrix4f projectionMatrix) throws IOException {
        this.shader = new EntityShader();
        shader.start();
        shader.doLoadMatrix(projectionMatrix, "projectionMatrix");
        shader.doLoadInt(5, "shadowMap");
        shader.doLoadInts(6, "shadowCube");
        shader.stop();
    }

    @Override
    public void render(Collection<? extends RenderObject> entities) {
        for (RenderObject entity : entities) {
            Model model = entity.getModel();
            checkCurrentBind(model);
            //load entity-specific data to shaders
            entity.prepareObject(getShader());
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

    @Override
    public void bindModel(Model model) {
        glBindVertexArray(model.getModelID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        //load model-specific data to shaders
        model.prepareShader(getShader());
    }

    public Shader getShader() {
        return shader;
    }
}
