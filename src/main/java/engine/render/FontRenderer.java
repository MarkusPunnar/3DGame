package engine.render;

import engine.model.Model;
import engine.shader.FontShader;
import engine.shader.Shader;
import object.RenderObject;
import org.lwjgl.opengl.GL11;

import java.util.Collection;

public class FontRenderer implements Renderer {

    private FontShader shader;

    public FontRenderer(FontShader shader) {
        this.shader = shader;
    }

    @Override
    public void render(Collection<? extends RenderObject> objects) {
        prepare();
        for (RenderObject object : objects) {
            object.prepareObject(shader);
            bindModel(object.getModel());
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, object.getModel().getVertexCount());
        }
        endRender();
    }

    private void endRender() {
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void prepare() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    @Override
    public Shader getShader() {
        return shader;
    }

    @Override
    public void bindModel(Model model) {
        model.prepareShader(shader);
    }
}
