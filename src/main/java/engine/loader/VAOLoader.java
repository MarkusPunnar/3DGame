package engine.loader;

import com.google.common.flogger.FluentLogger;
import com.google.common.flogger.StackSize;
import engine.model.RawModel;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import util.math.structure.Triangle;
import util.octree.BoundingBox;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class VAOLoader {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private List<Integer> VAOs;
    private List<Integer> VBOs;
    private List<Integer> textures;

    public VAOLoader() {
        this.VAOs = new ArrayList<>();
        this.VBOs = new ArrayList<>();
        this.textures = new ArrayList<>();
    }

    public RawModel loadToVAO(float[] positions, int[] indices, float[] normals, float[] textureCoords) {
        int vaoID = createVAO();
        VAOs.add(vaoID);
        storeIndicesInAttributeList(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        BoundingBox modelBoundingBox = createBoundingBox(positions);
        unbindVAO();
        return new RawModel(vaoID, indices.length, modelBoundingBox);
    }

    public int loadToVAO(float[] positions, float[] textureCoords) {
        int vaoID = createVAO();
        VAOs.add(vaoID);
        storeDataInAttributeList(0, 2, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        unbindVAO();
        return vaoID;
    }

    private BoundingBox createBoundingBox(float[] positions) {
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxZ = Float.MIN_VALUE;
        for (int i = 0; i < positions.length; i++) {
            int rem = i % 3;
            float coord = positions[i];
            switch (rem) {
                case 0:
                    if (coord > maxX) {
                        maxX = coord;
                    }
                    if (coord < minX) {
                        minX = coord;
                    }
                    break;
                case 1:
                    if (coord > maxY) {
                        maxY = coord;
                    }
                    if (coord < minY) {
                        minY = coord;
                    }
                    break;
                case 2:
                    if (coord > maxZ) {
                        maxZ = coord;
                    }
                    if (coord < minZ) {
                        minZ = coord;
                    }
                    break;
                default:
            }
        }
        return new BoundingBox(new Vector3f(minX, minY , minZ), new Vector3f(maxX, maxY, maxZ));
    }

    public List<Triangle> createTriangles(float[] positions, int[] indices) {
        List<Triangle> triangles = new ArrayList<>();
        for (int i = 0; i < indices.length;) {
            Vector3f[] vertices = new Vector3f[3];
            int current = 0;
            while (current < 3) {
                int index = indices[i];
                Vector3f vertex = new Vector3f(positions[3 * index], positions[3 * index + 1], positions[3 * index + 2]);
                vertices[i % 3] = vertex;
                current++;
                i++;
            }
            Triangle triangle = new Triangle(vertices);
            triangles.add(triangle);
        }
        return triangles;
    }

    public RawModel loadToVAO(float[] positions) {
        int vaoID = createVAO();
        VAOs.add(vaoID);
        storeDataInAttributeList(0, 2, positions);
        unbindVAO();
        return new RawModel(vaoID, positions.length / 2, null);
    }

    public int loadIconTexture(String fileName) throws IOException {
        return loadTexture("icons/" + fileName);
    }

    public int loadObjectTexture(String fileName) throws IOException {
        return loadTexture("objects/" + fileName);
    }

    public int loadGuiTexture(String fileName) throws IOException {
        return loadTexture("guis/" + fileName);
    }

    public int loadTerrainTexture(String fileName) throws IOException {
        return loadTexture("terrains/" + fileName);
    }

    public int loadFontAtlas(String fileName) throws IOException {
        return loadTexture("fonts/" + fileName);
    }

    private int loadTexture(String fileName) throws IOException {
        int textureID;
        int width, height;
        ByteBuffer image;
        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("textures/" + fileName + ".png")) {
                if (is == null) {
                    logger.atSevere().withStackTrace(StackSize.LARGE).log("Texture file %s was not found", fileName);
                    throw new IllegalArgumentException("Could not find resource from classpath: " + fileName);
                }
                try {
                    byte[] textureBytes = is.readAllBytes();
                    ByteBuffer textureBuffer = BufferUtils.createByteBuffer(textureBytes.length);
                    textureBuffer.put(textureBytes);
                    textureBuffer.flip();
                    image = STBImage.stbi_load_from_memory(textureBuffer, widthBuffer, heightBuffer, comp, 4);
                    if (image == null) {
                        logger.atSevere().withStackTrace(StackSize.LARGE).log("Image %s could not be loaded", fileName);
                        throw new IllegalArgumentException("Failed to load texture image: " + STBImage.stbi_failure_reason());
                    }
                    width = widthBuffer.get();
                    height = heightBuffer.get();
                } catch (IllegalArgumentException e) {
                    logger.atWarning().log("Tried to load texture from invalid file %s", fileName);
                    throw e;
                }
            }

        }
        textureID = glGenTextures();
        textures.add(textureID);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        return textureID;
    }


    private void storeDataInAttributeList(int attributeNumber, int coordSize, float[] data) {
        int vboID = glGenBuffers();
        VBOs.add(vboID);
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, coordSize, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void storeIndicesInAttributeList(int[] indices) {
        int vboID = glGenBuffers();
        VBOs.add(vboID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    public void cleanUp() {
        for (Integer vao : VAOs) {
            glDeleteVertexArrays(vao);
        }
        logger.atInfo().log("Cleaned up VAOs");
        for (Integer vbo : VBOs) {
            glDeleteBuffers(vbo);
        }
        logger.atInfo().log("Cleaned up VBOs");
        for (Integer texture : textures) {
            glDeleteTextures(texture);
        }
        logger.atInfo().log("Cleaned up textures");
    }

    private int createVAO() {
        int vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        return vaoID;
    }

    private void unbindVAO() {
        glBindVertexArray(0);
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
}
