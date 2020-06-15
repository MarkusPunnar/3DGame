package game.object.generation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.flogger.FluentLogger;
import com.google.common.flogger.StackSize;
import engine.DisplayManager;
import engine.loader.ObjectLoader;
import engine.loader.VAOLoader;
import engine.model.ModelCache;
import engine.model.ModelTexture;
import engine.model.RawModel;
import engine.model.TexturedModel;
import engine.model.data.ModelData;
import game.object.Entity;
import game.object.env.LightEntity;
import game.object.scene.Chest;
import game.object.scene.Door;
import game.state.Game;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import util.math.structure.Triangle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

public class EntityLoader {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public static void generateEntities(String fileName) throws IOException {
        JsonNode rootNode = parseJSON(fileName);
        loadEntities(rootNode);
    }

    private static void loadEntities(JsonNode rootNode) {
        Iterator<JsonNode> elements = rootNode.elements();
        while (elements.hasNext()) {
            JsonNode objectNode = elements.next();
            ForkJoinPool.commonPool().execute(() -> {
                try {
                    GL.setCapabilities(DisplayManager.getCapabilities());
                    EntityLoader.buildEntities(objectNode);
                } catch (IOException e) {
                    logger.atWarning().log("Entity loading failed due to IOException");
                }
            });
            elements.remove();
        }
    }

    private static JsonNode parseJSON(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString;
        try (InputStream is = ObjectLoader.class.getClassLoader().getResourceAsStream("assets/" + fileName + ".json")) {
            if (is == null) {
                logger.atSevere().withStackTrace(StackSize.LARGE).log("Object file tavern.json was not found");
                throw new IllegalArgumentException("Asset file not found");
            }
            StringBuilder sb = new StringBuilder();
            new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().forEach(sb::append);
            jsonString = sb.toString();
        }
        JsonNode rootNode = objectMapper.readTree(jsonString).get("objects");
        if (rootNode == null) {
            throw new IllegalArgumentException("Root node was not found");
        }
        return rootNode;
    }

    private static void buildEntities(JsonNode objectNode) throws IOException {
        TexturedModel model = getTexturedModel(objectNode.get("name").asText());
        TexturedModel secondaryModel = objectNode.has("secondary") ? getTexturedModel(objectNode.get("secondary").asText()) : null;
        String type = objectNode.get("type").asText();
        Map<String, Vector3f> objectData = new HashMap<>();
        addIfExists(objectData, objectNode, "scale");
        addIfExists(objectData, objectNode, "colour");
        addIfExists(objectData, objectNode, "attenuation");
        Iterator<JsonNode> instances = objectNode.get("instances").elements();
        while (instances.hasNext()) {
            JsonNode instanceNode = instances.next();
            Entity entity = null;
            switch (type) {
                case "entity": {
                    entity = Entity.build(model, instanceNode, objectData);
                    if (objectNode.has("hitbox")) {
                        ModelData bedModelData = ObjectLoader.loadObjectModel("hitbox/bedbox");
                        List<Triangle> hitboxTriangles = Game.getInstance().getLoader().createTriangles(bedModelData.getVertices(), bedModelData.getIndices());
                        entity.setTriangles(hitboxTriangles);
                        GenerationUtil.setParentObject(entity);
                    }
                    break;
                }
                case "door":
                    entity = Door.build(model, instanceNode, objectData);
                    break;
                case "chest":
                    entity = Chest.build(model, secondaryModel, instanceNode, objectData);
                    break;
                case "lightentity":
                    entity = LightEntity.build(model, instanceNode, objectData);
                    break;
                default:
                    logger.atWarning().log("Unknown entity type %s", type);
            }
            Game.getInstance().getRenderer().processEntity(entity);
            Game.getInstance().getActiveObjects().add(entity);
            instances.remove();
        }
    }

    public static Vector3f getVectorFromNode(JsonNode node) {
        float x = node.has("x") ? node.get("x").floatValue() : 0;
        float y = node.has("y") ? node.get("y").floatValue() : 0;
        float z = node.has("z") ? node.get("z").floatValue() : 0;
        return new Vector3f(x, y, z);
    }

    private static void addIfExists(Map<String, Vector3f> data, JsonNode node, String attributeName) {
        boolean exists = node.has(attributeName);
        if (exists) {
            data.put(attributeName, getVectorFromNode(node));
        }
    }

    protected static TexturedModel getTexturedModel(String objName, ModelTexture texture) throws IOException {
        ModelData modelData = ObjectLoader.loadObjectModel(objName);
        RawModel rawModel = Game.getInstance().getLoader().loadToVAO(modelData.getVertices(), modelData.getIndices(), modelData.getNormals(), modelData.getTextureCoords());
        return new TexturedModel(rawModel, texture);
    }

    private static TexturedModel getTexturedModel(String fileName) throws IOException {
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
}
