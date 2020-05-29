package object.generation;

import com.google.common.flogger.FluentLogger;
import engine.loader.TerrainLoader;
import engine.loader.VAOLoader;
import engine.model.data.TerrainData;
import object.terrain.TerrainTexture;
import object.terrain.TerrainTexturePack;
import game.state.Game;
import object.terrain.Terrain;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class TerrainGenerator implements Generator {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    @Override
    public List<Terrain> generate() throws IOException, URISyntaxException {
        VAOLoader loader = Game.getInstance().getLoader();
        List<Terrain> terrains = new ArrayList<>();
        TerrainData test = TerrainLoader.loadTerrainModel("terrain");
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTerrainTexture("grass"));
        TerrainTexture greenTexture = new TerrainTexture(loader.loadTerrainTexture("moss"));
        TerrainTexture redTexture = new TerrainTexture(loader.loadTerrainTexture("mud"));
        TerrainTexture blueTexture = new TerrainTexture(loader.loadTerrainTexture("path"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, redTexture, greenTexture, blueTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTerrainTexture("blendMap"));
        terrains.add(new Terrain(-1, -1, loader, test, texturePack, blendMap));
        terrains.add(new Terrain(0,0, loader, test, texturePack, blendMap));
        terrains.add(new Terrain(-1,0, loader, test, texturePack, blendMap));
        terrains.add(new Terrain(0,-1, loader, test ,texturePack, blendMap));
        logger.atInfo().log("%s - Entities generated", getClass().getSimpleName());
        return terrains;
    }
}
