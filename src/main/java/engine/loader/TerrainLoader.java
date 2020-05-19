package engine.loader;

import engine.model.data.TerrainData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TerrainLoader {

    private static int VERTEX_COUNT = 129;

    public static TerrainData loadTerrainModel(String fileName) throws URISyntaxException, IOException {
        float[][] heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        URL location = TerrainLoader.class.getClassLoader().getResource("models/terrains/" + fileName + ".obj");
        if (location == null) {
            throw new IllegalArgumentException("Terrain model file " + fileName + " not found");
        }
        List<String> lines = Files.readAllLines(Paths.get(location.toURI()));
        for (String line : lines) {
            String[] lineParts = line.split(" ");
            String type = lineParts[0];
            switch (type) {
                case "v":
                    float x = Float.parseFloat(lineParts[1]);
                    float y = Float.parseFloat(lineParts[2]);
                    float z = Float.parseFloat(lineParts[3]);
                    int j = (int) ((128 + x) / 2);
                    int i = (int) ((128 + z) / 2);
                    heights[i][j] = y;
                    break;
                case "vn":
                    break;
                default:
            }
        }
        return new TerrainData(heights, null);
    }


}
