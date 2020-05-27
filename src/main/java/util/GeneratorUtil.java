package util;

import engine.loader.ObjectLoader;
import engine.loader.VAOLoader;
import engine.model.RawModel;
import engine.model.TexturedModel;
import engine.model.data.ModelData;
import engine.texture.ModelTexture;
import game.state.Game;
import object.RenderObject;
import util.math.structure.Triangle;

import java.io.IOException;
import java.net.URISyntaxException;

public class GeneratorUtil {

    public static void setParentObject(RenderObject object) {
        for (Triangle triangle : object.getModel().getRawModel().getTriangles()) {
            triangle.setParentObject(object);
        }
    }

    public static TexturedModel getTexturedModel(String objName, ModelTexture texture) throws IOException, URISyntaxException {
        ModelData modelData = ObjectLoader.loadObjectModel(objName);
        RawModel rawModel = Game.getInstance().getLoader().loadToVAO(modelData.getVertices(), modelData.getIndices(), modelData.getNormals(), modelData.getTextureCoords());
        return new TexturedModel(rawModel, texture);
    }

    public static TexturedModel getTexturedModel(String fileName) throws URISyntaxException, IOException {
        VAOLoader loader = Game.getInstance().getLoader();
        ModelData modelData = ObjectLoader.loadObjectModel(fileName);
        RawModel rawModel = loader.loadToVAO(modelData.getVertices(), modelData.getIndices(), modelData.getNormals(), modelData.getTextureCoords());
        ModelTexture texture = new ModelTexture(loader.loadObjectTexture(fileName));
        return new TexturedModel(rawModel, texture);
    }
}
