package object.scene.generation;

import engine.loader.TerrainLoader;
import engine.loader.VAOLoader;
import engine.model.data.TerrainData;
import engine.texture.TerrainTexture;
import engine.texture.TerrainTexturePack;
import object.env.Light;
import object.terrain.Terrain;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class TerrainGenerator implements Generator {

    private VAOLoader loader;

    public TerrainGenerator(VAOLoader loader) {
        this.loader = loader;
    }

    @Override
    public List<Terrain> generate(List<Light> lights) throws IOException, URISyntaxException {
        List<Terrain> terrains = new ArrayList<>();
        TerrainData test = TerrainLoader.loadTerrainModel("terrain");
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTerrainTexture("grass"));
        TerrainTexture greenTexture = new TerrainTexture(loader.loadTerrainTexture("moss"));
        TerrainTexture redTexture = new TerrainTexture(loader.loadTerrainTexture("mud"));
        TerrainTexture blueTexture = new TerrainTexture(loader.loadTerrainTexture("path"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, redTexture, greenTexture, blueTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTerrainTexture("blendMap"));
        terrains.add(new Terrain(-1, -1, loader, test, texturePack, blendMap));
//        terrains.add(new Terrain(0,0, loader, test, texturePack, blendMap));
//        terrains.add(new Terrain(-1,0, loader, texturePack, blendMap));
//        terrains.add(new Terrain(0,-1, loader, texturePack, blendMap));
        return terrains;
    }
}
