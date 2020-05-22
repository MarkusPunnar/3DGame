package engine.render;

import engine.model.Model;
import engine.shader.Shader;
import engine.shader.TerrainShader;
import object.RenderObject;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.Collection;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class TerrainRenderer implements Renderer {

    private TerrainShader shader;

    public TerrainRenderer(Matrix4f projectionMatrix) throws IOException {
        this.shader = new TerrainShader();
        shader.start();
        shader.doLoadMatrix(projectionMatrix, "projectionMatrix");
        shader.connectTextureUnits();
        shader.stop();
    }

    public void render(Collection<? extends RenderObject> terrains) {
        for (RenderObject terrain : terrains) {
            bindModel(terrain.getModel());
            //load entity-specific data to shaders
            terrain.prepareObject(shader);
            glDrawElements(GL_TRIANGLES, terrain.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
            unbindModel();
        }
    }

    public Shader getShader() {
        return shader;
    }

    @Override
    public void bindModel(Model model) {
        glBindVertexArray(model.getModelID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        //load model-specific data to shaders
        model.prepareShader(shader);
    }
}
