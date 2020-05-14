package engine.loader;

import engine.model.RawModel;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import util.octree.BoundingBox;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Loader {

    private List<Integer> VAOs;
    private List<Integer> VBOs;
    private List<Integer> textures;

    public Loader() {
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

    public RawModel loadToVAO(float[] positions) {
        int vaoID = createVAO();
        VAOs.add(vaoID);
        storeDataInAttributeList(0, 2, positions);
        unbindVAO();
        return new RawModel(vaoID, positions.length / 2, null);
    }

    public int loadTexture(String fileName) {
        int textureID;
        int width, height;
        ByteBuffer image;
        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            URL location = getClass().getClassLoader().getResource("textures/" + fileName + ".png");
            if (location == null) {
                throw new IllegalArgumentException("Could not find resource from classpath: " + fileName);
            }
            try {
                image = STBImage.stbi_load(Paths.get(location.toURI()).toString(), widthBuffer, heightBuffer, comp, 4);
                if (image == null) {
                    throw new IllegalArgumentException("Failed to load texture image: " + STBImage.stbi_failure_reason());
                }
                width = widthBuffer.get();
                height = heightBuffer.get();
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid URI for texture " + fileName);
            }
        }
        textureID = glGenTextures();
        textures.add(textureID);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); //sets MINIFICATION filtering to nearest
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); //sets MAGNIFICATION filtering to nearest
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        return textureID;
    }


    private void storeDataInAttributeList(int attributeNumber, int coordSize, float[] data) {
        int vboID = glGenBuffers(); //Create VBO
        VBOs.add(vboID);
        glBindBuffer(GL_ARRAY_BUFFER, vboID); //Bind the VBO
        FloatBuffer buffer = storeDataInFloatBuffer(data); //Create FloatBuffer
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW); //Write data to VBO
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
        for (Integer vbo : VBOs) {
            glDeleteBuffers(vbo);
        }
        for (Integer texture : textures) {
            glDeleteTextures(texture);
        }
    }

    private int createVAO() {
        int vaoID = glGenVertexArrays(); //Create VAO
        glBindVertexArray(vaoID); //Bind VAO
        return vaoID;
    }

    private void unbindVAO() {
        glBindVertexArray(0); //Unbind VAO
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip(); //Flip buffer so it can be read from
        return buffer;
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
}
