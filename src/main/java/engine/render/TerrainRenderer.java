package engine.render;

import engine.model.RawModel;
import engine.model.TexturedModel;
import engine.texture.TerrainTexturePack;
import object.terrain.Terrain;
import org.joml.Matrix4f;
import engine.shader.Shader;
import engine.shader.TerrainShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import util.math.MathUtil;

import java.util.Collection;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
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
            prepareObject(terrain);
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

    public void prepareObject(RenderObject object) {
        bindModel(object.getTexturedModel());
        bindTextures((Terrain) object);
        Matrix4f transformationMatrix = MathUtil.createTransformationMatrix(object.getPosition(), object.getRotation(), object.getScaleVector());
        shader.doLoadMatrix(transformationMatrix, "transformationMatrix");
    }

    private void bindTextures(Terrain terrain) {
        TerrainTexturePack pack = terrain.getTexturePack();
        GL13.glActiveTexture(GL_TEXTURE0);
        GL11.glBindTexture(GL_TEXTURE_2D, pack.getBackgroundTexture().getTextureID());
        GL13.glActiveTexture(GL_TEXTURE1);
        GL11.glBindTexture(GL_TEXTURE_2D, pack.getRedTexture().getTextureID());
        GL13.glActiveTexture(GL_TEXTURE2);
        GL11.glBindTexture(GL_TEXTURE_2D, pack.getGreenTexture().getTextureID());
        GL13.glActiveTexture(GL_TEXTURE3);
        GL11.glBindTexture(GL_TEXTURE_2D, pack.getBlueTexture().getTextureID());
        GL13.glActiveTexture(GL_TEXTURE4);
        GL11.glBindTexture(GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
    }
}
