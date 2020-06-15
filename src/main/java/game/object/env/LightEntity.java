package game.object.env;

import com.fasterxml.jackson.databind.JsonNode;
import engine.model.TexturedModel;
import game.object.Entity;
import game.object.generation.EntityLoader;
import org.joml.Vector3f;
import util.octree.BoundingBox;

import java.util.Map;

public class LightEntity extends Entity {

    private final Light light;

    private LightEntity(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scale, Light light) {
        super(model, position, rotation, scale);
        this.light = light;
    }

    public static LightEntity build(TexturedModel model, JsonNode attributeNode, Map<String, Vector3f> objectData) {
        JsonNode positionNode = attributeNode.get("position");
        Vector3f entityPosition = EntityLoader.getVectorFromNode(positionNode);
        Vector3f rotation = attributeNode.has("rotation") ? EntityLoader.getVectorFromNode(attributeNode.get("rotation")) : new Vector3f();
        Vector3f offset = EntityLoader.getVectorFromNode(attributeNode.get("offset"));
        Vector3f lightPosition = entityPosition.add(offset, new Vector3f());
        boolean isPointLight = attributeNode.get("pointlight").asBoolean();
        BoundingBox boundingBox = new BoundingBox(EntityLoader.getVectorFromNode(attributeNode.get("regionMin")),
                EntityLoader.getVectorFromNode(attributeNode.get("regionMax")));
        Light light = new Light(lightPosition, objectData.get("colour"), objectData.get("attenuation"), isPointLight, boundingBox);
        Vector3f scale = objectData.containsKey("scale") ? objectData.get("scale") : new Vector3f(1);
        return new LightEntity(model, entityPosition, rotation, scale, light);
    }

    public Light getLight() {
        return light;
    }
}
