package game.object.generation;

import engine.model.TexturedModel;
import game.object.Entity;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CampGenerator {

//    public List<Entity> generate() throws IOException {
//        List<Entity> campEntities = new ArrayList<>();
//        campEntities.addAll(generateWall());
//        return campEntities;
//    }
//
//    private List<Entity> generateWall() throws IOException {
//        List<Entity> spikes = new ArrayList<>();
//        List<Vector2f> ellipsePoints = GenerationUtil.generateEllipsePoints(1000, 420, 450);
//        TexturedModel spikeModel = EntityLoader.getTexturedModel("spike");
//        for (Vector2f point : ellipsePoints) {
//            spikes.add(new Entity.Builder(spikeModel, new Vector3f(point.x - 120, 0, point.y - 150)).build());
//        }
//        return spikes;
//    }
}
