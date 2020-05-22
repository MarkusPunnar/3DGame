package engine.model;

import engine.shader.Shader;
import engine.texture.ModelTexture;
import util.OpenGLUtil;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class TexturedModel implements Model {

    private RawModel rawModel;
    private ModelTexture texture;

    public TexturedModel(RawModel rawModel, ModelTexture texture) {
        this.rawModel = rawModel;
        this.texture = texture;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public int getModelID() {
        return rawModel.getVaoID();
    }

    public int getVertexCount() {
        return rawModel.getVertexCount();
    }

    public ModelTexture getTexture() {
        return texture;
    }

    @Override
    public void prepareShader(Shader shader) {
        shader.doLoadFloat(texture.getReflectivity(), "reflectivity");
        shader.doLoadFloat(texture.getShineDamper(), "shineDamper");
        float fakeLighting = texture.useFakeLighting() ? 1 : 0;
        shader.doLoadFloat(fakeLighting, "fakeLighting");
        if (texture.isTransparent()) {
            OpenGLUtil.disableCulling();
        }
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, getTexture().getTextureID());
    }
}
