package engine.render;

import engine.model.RawModel;
import engine.model.TexturedModel;
import org.joml.Matrix4f;
import engine.shader.Shader;
import engine.shader.TerrainShader;
import util.math.MathUtil;

import java.util.Collection;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class TerrainRenderer implements Renderer {

    private TerrainShader shader;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.doLoadMatrix(projectionMatrix, "projectionMatrix");
        shader.stop();
    }

    public void render(Collection<RenderObject> terrains) {
        for (RenderObject terrain : terrains) {
            TexturedModel texturedModel = terrain.getTexturedModel();
            bindTexturedModel(texturedModel);
            prepareObject(terrain);
            glDrawElements(GL_TRIANGLES, texturedModel.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
            unbindModel();
        }
    }

    public Shader getShader() {
        return shader;
    }

    @Override
    public void bindTexturedModel(TexturedModel texturedModel) {
        RawModel rawModel = texturedModel.getRawModel();
        glBindVertexArray(rawModel.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        shader.loadShineVariables(texturedModel.getTexture().getReflectivity(), texturedModel.getTexture().getShineDamper());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texturedModel.getTexture().getTextureID());
    }

    public void prepareObject(RenderObject terrain) {
        Matrix4f transformationMatrix = MathUtil.createTransformationMatrix(terrain.getPosition(), terrain.getRotation(), terrain.getScaleVector());
        shader.doLoadMatrix(transformationMatrix, "transformationMatrix");
    }
}
