package engine.render;

import engine.model.Model;
import engine.model.RawModel;
import engine.shader.GuiShader;
import engine.shader.Shader;
import game.state.Game;
import game.object.RenderObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.util.Collection;

public class GuiRenderer implements Renderer {

    private final RawModel quadModel;
    private Shader shader;

    public GuiRenderer() throws IOException {
        float[] positions = new float[]{-1, 1, -1, -1, 1, 1, 1, -1};
        this.quadModel = Game.getInstance().getLoader().loadToVAO(positions);
        this.shader = new GuiShader();
    }

    @Override
    public void render(Collection<? extends RenderObject> objects) {
        prepare();
        for (RenderObject object : objects) {
            object.prepareObject(shader);
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quadModel.getVertexCount());
        }
        endRender();
    }

    private void prepare() {
        GL30.glBindVertexArray(quadModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private void endRender() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }


    @Override
    public Shader getShader() {
        return shader;
    }

    @Override
    public void bindModel(Model model) {
    }
}
