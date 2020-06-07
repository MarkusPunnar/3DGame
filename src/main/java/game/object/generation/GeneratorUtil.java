package game.object.generation;

import com.google.common.flogger.FluentLogger;
import engine.loader.ObjectLoader;
import engine.loader.VAOLoader;
import engine.model.ModelCache;
import engine.model.RawModel;
import engine.model.TexturedModel;
import engine.model.data.ModelData;
import engine.model.ModelTexture;
import engine.texture.TextureCache;
import game.state.Game;
import game.object.RenderObject;
import org.joml.Vector2f;
import util.math.structure.Triangle;

import java.io.IOException;

public class GeneratorUtil {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public static void setParentObject(RenderObject object) {
        for (Triangle triangle : object.getTriangles()) {
            triangle.setParentObject(object);
        }
    }

    public static TexturedModel getTexturedModel(String objName, ModelTexture texture) throws IOException {
        ModelData modelData = ObjectLoader.loadObjectModel(objName);
        RawModel rawModel = Game.getInstance().getLoader().loadToVAO(modelData.getVertices(), modelData.getIndices(), modelData.getNormals(), modelData.getTextureCoords());
        return new TexturedModel(rawModel, texture);
    }

    public static TexturedModel getTexturedModel(String fileName) throws IOException {
        ModelCache modelCache = Game.getInstance().getModelCache();
        TexturedModel cachedModel = modelCache.getByName(fileName);
        if (cachedModel != null) {
            logger.atInfo().log("Found cached model for model %s", fileName);
            return cachedModel;
        }
        VAOLoader loader = Game.getInstance().getLoader();
        ModelData modelData = ObjectLoader.loadObjectModel(fileName);
        RawModel rawModel = loader.loadToVAO(modelData.getVertices(), modelData.getIndices(), modelData.getNormals(), modelData.getTextureCoords());
        ModelTexture texture = new ModelTexture(loader.loadObjectTexture(fileName));
        TexturedModel texturedModel = new TexturedModel(rawModel, texture);
        modelCache.addModel(fileName, texturedModel);
        return texturedModel;
    }

    public static Vector2f fromOpenGLCoords(float x, float y) {
       return new Vector2f((1 + x) / 2f,  Math.abs(y - 1) / 2f);
    }


    public static int getTextureFromCache(String textureName) throws IOException {
        VAOLoader loader = Game.getInstance().getLoader();
        TextureCache textureCache = Game.getInstance().getTextureCache();
        Integer textureID = textureCache.getByName(textureName);
        if (textureID == null) {
            textureID = loader.loadGuiTexture(textureName);
            textureCache.addTexture(textureName, textureID);
        }
        return textureID;
    }
}
