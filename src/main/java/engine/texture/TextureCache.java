package engine.texture;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {

    private Map<String, Integer> textureCache;

    public TextureCache() {
        this.textureCache = new HashMap<>();
    }

    public Integer getByName(String name) {
        return textureCache.get(name);
    }

    public void addTexture(String name, Integer textureID) {
        textureCache.put(name, textureID);
    }
}
