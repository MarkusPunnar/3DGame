package engine.render;

import engine.model.RawModel;
import engine.model.TexturedModel;
import org.joml.Matrix4f;
import engine.shader.StaticShader;
import util.OpenGLUtil;

import java.util.Collection;

import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;


public class EntityRenderer implements Renderer {

    private StaticShader shader;
    private TexturedModel currentTexture;

    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.doLoadMatrix(projectionMatrix, "projectionMatrix");
        shader.stop();
    }

    public void render(Collection<? extends RenderObject> entities) {
        for (RenderObject entity : entities) {
            TexturedModel texturedModel = entity.getTexturedModel();
            checkCurrentBind(texturedModel);
            entity.prepareObject(shader);
            glDrawElements(GL_TRIANGLES, texturedModel.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
        }
        unbindModel();
        currentTexture = null;
    }

    private void checkCurrentBind(TexturedModel texturedModel) {
        if (currentTexture == null || !currentTexture.equals(texturedModel)) {
            unbindModel();
            bindModel(texturedModel);
            currentTexture = texturedModel;
        }
    }

    public void bindModel(TexturedModel texturedModel) {
        RawModel rawModel = texturedModel.getRawModel();
        glBindVertexArray(rawModel.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        shader.loadShineVariables(texturedModel.getTexture().getReflectivity(), texturedModel.getTexture().getShineDamper());
        shader.loadFakeLighting(texturedModel.getTexture().useFakeLighting());
        if (texturedModel.getTexture().isTransparent()) {
            OpenGLUtil.disableCulling();
        }
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texturedModel.getTexture().getTextureID());
    }

    public StaticShader getShader() {
        return shader;
    }
}
