package engine.render;

import engine.model.RawModel;
import engine.model.TexturedModel;
import engine.shader.Shader;
import engine.shader.TerrainShader;
import org.joml.Matrix4f;

import java.util.Collection;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class TerrainRenderer implements Renderer {

    private TerrainShader shader;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.doLoadMatrix(projectionMatrix, "projectionMatrix");
        shader.connectTextureUnits();
        shader.stop();
    }

    public void render(Collection<? extends RenderObject> terrains) {
        for (RenderObject terrain : terrains) {
            bindModel(terrain.getTexturedModel());
            terrain.prepareObject(shader);
            glDrawElements(GL_TRIANGLES, terrain.getTexturedModel().getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
            unbindModel();
        }
    }

    public Shader getShader() {
        return shader;
    }

    @Override
    public void bindModel(TexturedModel texturedModel) {
        RawModel rawModel = texturedModel.getRawModel();
        glBindVertexArray(rawModel.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        shader.loadShineVariables(1, 0);
    }
}
