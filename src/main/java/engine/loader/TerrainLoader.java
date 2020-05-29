package engine.loader;

import com.google.common.flogger.FluentLogger;
import com.google.common.flogger.StackSize;
import engine.model.data.TerrainData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class TerrainLoader {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private static int VERTEX_COUNT = 129;

    public static TerrainData loadTerrainModel(String fileName) throws IOException {
        float[][] heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        List<String> lines;
        try (InputStream is = ObjectLoader.class.getClassLoader().getResourceAsStream("models/terrains/" + fileName + ".obj")) {
            if (is == null) {
                logger.atSevere().withStackTrace(StackSize.LARGE).log("Object file %s was not found", fileName);
                throw new IllegalArgumentException("Object model file not found");
            }
            lines = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
        }
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
        logger.atInfo().log("Terrain model from %s was loaded successfully", fileName);
        return new TerrainData(heights, null);
    }


}
