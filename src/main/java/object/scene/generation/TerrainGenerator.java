package object.scene.generation;

import engine.loader.Loader;
import engine.texture.TerrainTexture;
import engine.texture.TerrainTexturePack;
import object.terrain.Terrain;

import java.util.ArrayList;
import java.util.List;

public class TerrainGenerator implements Generator {

    private Loader loader;

    public TerrainGenerator(Loader loader) {
        this.loader = loader;
    }

    @Override
    public List<Terrain> generate() {
        List<Terrain> terrains = new ArrayList<>();
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTerrainTexture("grass"));
        TerrainTexture greenTexture = new TerrainTexture(loader.loadTerrainTexture("moss"));
        TerrainTexture redTexture = new TerrainTexture(loader.loadTerrainTexture("mud"));
        TerrainTexture blueTexture = new TerrainTexture(loader.loadTerrainTexture("path"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, redTexture, greenTexture, blueTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTerrainTexture("blendMap"));
        terrains.add(new Terrain(-1,-1, loader, texturePack, blendMap));
        terrains.add(new Terrain(0,0, loader, texturePack, blendMap));
        terrains.add(new Terrain(-1,0, loader, texturePack, blendMap));
        terrains.add(new Terrain(0,-1, loader, texturePack, blendMap));
        return terrains;
    }
}
