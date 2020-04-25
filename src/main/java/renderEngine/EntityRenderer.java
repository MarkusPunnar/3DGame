package renderEngine;

import entity.RenderObject;
import model.RawModel;
import model.TexturedModel;
import org.joml.Matrix4f;
import shader.StaticShader;
import util.math.MathUtil;

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

    public void render(Collection<RenderObject> entities) {
        for (RenderObject entity : entities) {
            TexturedModel texturedModel = entity.getTexturedModel();
            checkCurrentBind(texturedModel);
            prepareObject(entity);
            glDrawElements(GL_TRIANGLES, texturedModel.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
        }
        unbindModel();
        currentTexture = null;
    }

    private void checkCurrentBind(TexturedModel texturedModel) {
        if (currentTexture == null || !currentTexture.equals(texturedModel)) {
            unbindModel();
            bindTexturedModel(texturedModel);
            currentTexture = texturedModel;
        }
    }

    public void bindTexturedModel(TexturedModel texturedModel) {
        RawModel rawModel = texturedModel.getRawModel();
        glBindVertexArray(rawModel.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        shader.loadShineVariables(texturedModel.getTexture().getReflectivity(), texturedModel.getTexture().getShineDamper());
        shader.loadFakeLighting(texturedModel.getTexture().useFakeLighting());
        if (texturedModel.getTexture().isTransparent()) {
            ParentRenderer.disableCulling();
        }
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texturedModel.getTexture().getTextureID());
    }

    public void prepareObject(RenderObject entity) {
        Matrix4f transformationMatrix = MathUtil.createTransformationMatrix(entity.getPosition(), entity.getRotationX(), entity.getRotationY(), entity.getRotationZ(), entity.getScaleVector());
        shader.doLoadMatrix(transformationMatrix, "transformationMatrix");
    }

    public StaticShader getShader() {
        return shader;
    }
}