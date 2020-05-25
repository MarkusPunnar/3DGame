package engine.shadow;

import com.google.common.flogger.FluentLogger;
import org.lwjgl.opengl.GL32;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30.*;

public class ShadowFrameBuffer {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public static final int SHADOW_WIDTH = 4096;
    public static final int SHADOW_HEIGHT = 4096;

    private int fboID;
    private int depthMapTextureID;
    private boolean hasCubeMapTexture;

    public ShadowFrameBuffer(boolean isCubeMap) {
        fboID = glGenFramebuffers();
        if (isCubeMap) {
            depthMapTextureID = createCubeMapDepthTexture();
        } else {
            depthMapTextureID = create2DDepthTexture();
        }

        this.hasCubeMapTexture = isCubeMap;
        initFrameBuffer();
        logger.atInfo().log("Initialized framebuffer - isCubeMap %s", isCubeMap);
    }

    private void initFrameBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
        if (hasCubeMapTexture) {
            GL32.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthMapTextureID, 0);
        } else {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMapTextureID, 0);
        }
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private int create2DDepthTexture() {
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

    private int createCubeMapDepthTexture() {
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
        for (int i = 0; i < 6; i++) {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT,
                    0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        }
        return textureID;
    }

    public int getFboID() {
        return fboID;
    }

    public int getDepthMapTextureID() {
        return depthMapTextureID;
    }
}
