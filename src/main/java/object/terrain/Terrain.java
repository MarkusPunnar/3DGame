package object.terrain;

import engine.render.RenderObject;
import engine.model.RawModel;
import engine.model.TexturedModel;
import org.joml.Vector3f;
import engine.loader.Loader;
import engine.texture.ModelTexture;

public class Terrain implements RenderObject {

    private static final float SIZE = 800;
    private static final int VERTICES = 128;

    private float x;
    private float z;
    private TexturedModel texturedModel;

    public Terrain(int gridX, int gridZ, Loader loader, ModelTexture texture) {
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.texturedModel = new TexturedModel(generateTerrain(loader), texture);
    }

    private RawModel generateTerrain(Loader loader) {
        int count = VERTICES * VERTICES;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTICES - 1) * (VERTICES - 1)];
        int vertexPointer = 0;
        for (int i = 0; i < VERTICES; i++) {
            for (int j = 0; j < VERTICES; j++) {
                vertices[vertexPointer * 3] = (float) j / ((float) VERTICES - 1) * SIZE;
                vertices[vertexPointer * 3 + 1] = 0;
                vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTICES - 1) * SIZE;
                normals[vertexPointer * 3] = 0;
                normals[vertexPointer * 3 + 1] = 1;
                normals[vertexPointer * 3 + 2] = 0;
                textureCoords[vertexPointer * 2] = (float) j / ((float) VERTICES - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTICES - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < VERTICES - 1; gz++) {
            for (int gx = 0; gx < VERTICES - 1; gx++) {
                int topLeft = (gz * VERTICES) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTICES) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, indices, normals, textureCoords);
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public Vector3f getPosition() {
        return new Vector3f(x, 0, z);
    }

    public float getRotationX() {
        return 0;
    }

    public float getRotationY() {
        return 0;
    }

    public float getRotationZ() {
        return 0;
    }

    public Vector3f getScaleVector() {
        return new Vector3f(1);
    }

    public TexturedModel getTexturedModel() {
        return texturedModel;
    }

    public int getID() {
        return texturedModel.getModelID();
    }
}
