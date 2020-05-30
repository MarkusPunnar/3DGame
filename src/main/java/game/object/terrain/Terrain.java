package game.object.terrain;

import com.google.common.flogger.FluentLogger;
import engine.model.TerrainModel;
import engine.model.data.TerrainData;
import game.object.RenderObject;
import engine.model.RawModel;
import engine.model.TexturedModel;
import engine.shader.Shader;
import game.ui.ObjectType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import engine.loader.VAOLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import util.GeneratorUtil;
import util.math.MathUtil;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.*;

public class Terrain extends RenderObject {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private static final float SIZE = 400;
    private static final int VERTICES = 129;

    private final float x;
    private final float z;
    private final TexturedModel texturedModel;
    private final TerrainTexturePack texturePack;
    private final TerrainTexture blendMap;

    private boolean texturesBound;

    public Terrain(int gridX, int gridZ, VAOLoader loader, TerrainData data, TerrainTexturePack pack, TerrainTexture blendMap) {
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.texturedModel = new TerrainModel(generateTerrain(loader, data), null);
        this.texturePack = pack;
        this.blendMap = blendMap;
        this.texturesBound = false;
        GeneratorUtil.setParentObject(this);
    }

    private RawModel generateTerrain(VAOLoader loader, TerrainData data) {
        int count = VERTICES * VERTICES;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTICES - 1) * (VERTICES - 1)];
        int vertexPointer = 0;
        for (int i = 0; i < VERTICES; i++) {
            for (int j = 0; j < VERTICES; j++) {
                vertices[vertexPointer * 3] = (float) j / ((float) VERTICES - 1) * SIZE;
//                vertices[vertexPointer * 3 + 1] = data.getHeights()[i][j];
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
        logger.atInfo().log("Generated terrain tile at coordinates (%f, %f)", x, z);
        return loader.loadToVAO(vertices, indices, normals, textureCoords);
    }


    public void prepareObject(Shader shader) {
        bindTextures();
        Matrix4f transformationMatrix = MathUtil.createTransformationMatrix(getPosition(), getRotation(), getScaleVector());
        shader.doLoadMatrix(transformationMatrix, "transformationMatrix");
    }

    private void bindTextures() {
        GL13.glActiveTexture(GL_TEXTURE0);
        GL11.glBindTexture(GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
        if (texturesBound) {
            return;
        }
        GL13.glActiveTexture(GL_TEXTURE1);
        GL11.glBindTexture(GL_TEXTURE_2D, texturePack.getRedTexture().getTextureID());
        GL13.glActiveTexture(GL_TEXTURE2);
        GL11.glBindTexture(GL_TEXTURE_2D, texturePack.getGreenTexture().getTextureID());
        GL13.glActiveTexture(GL_TEXTURE3);
        GL11.glBindTexture(GL_TEXTURE_2D, texturePack.getBlueTexture().getTextureID());
        GL13.glActiveTexture(GL_TEXTURE4);
        GL11.glBindTexture(GL_TEXTURE_2D, blendMap.getTextureID());
        logger.atInfo().log("Binded terrain blendmap textures");
        texturesBound = true;
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public Vector3f getPosition() {
        return new Vector3f(x, -0.1f, z);
    }

    public Vector3f getRotation() {
        return new Vector3f();
    }

    public Vector3f getScaleVector() {
        return new Vector3f(1);
    }

    public TexturedModel getModel() {
        return texturedModel;
    }

    public int getID() {
        return texturedModel.getModelID();
    }

    public ObjectType getType() {
        return ObjectType.TERRAIN;
    }
}
