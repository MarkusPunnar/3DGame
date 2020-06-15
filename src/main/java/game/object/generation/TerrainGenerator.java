package game.object.generation;

import com.google.common.flogger.FluentLogger;
import engine.loader.TerrainLoader;
import engine.loader.VAOLoader;
import engine.model.data.TerrainData;
import game.object.terrain.TerrainTexture;
import game.object.terrain.TerrainTexturePack;
import game.state.Game;
import game.object.terrain.Terrain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TerrainGenerator  {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public List<Terrain> generateTerrain() throws IOException {
        VAOLoader loader = Game.getInstance().getLoader();
        List<Terrain> terrains = new ArrayList<>();
        //TODO: Async
        TerrainData test = TerrainLoader.loadTerrainModel("terrain");
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTerrainTexture("grass"));
        TerrainTexture greenTexture = new TerrainTexture(loader.loadTerrainTexture("moss"));
        TerrainTexture redTexture = new TerrainTexture(loader.loadTerrainTexture("mud"));
        TerrainTexture blueTexture = new TerrainTexture(loader.loadTerrainTexture("path"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, redTexture, greenTexture, blueTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTerrainTexture("blendMap"));
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                terrains.add(new Terrain(i, j, loader, test, texturePack, blendMap));
            }
        }
        logger.atInfo().log("%s - Entities generated", getClass().getSimpleName());
        return terrains;
    }
}
