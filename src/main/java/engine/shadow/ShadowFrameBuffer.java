package engine.shadow;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30.*;

public class ShadowFrameBuffer {

    public static final int SHADOW_WIDTH = 1024 * 2;
    public static final int SHADOW_HEIGHT = 1024 * 2;

    private int fboID;
    private int depthMapTextureID;

    public ShadowFrameBuffer() {
        fboID = glGenFramebuffers();
        depthMapTextureID = createDepthTexture();
        initFrameBuffer();
    }

    private void initFrameBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMapTextureID, 0);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private int createDepthTexture() {
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, new float[]{ 1.0f, 1.0f, 1.0f, 1.0f });
        return textureID;
    }

    public int getFboID() {
        return fboID;
    }

    public int getDepthMapTextureID() {
        return depthMapTextureID;
    }
}
