package engine.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModelCache {

    private Map<String, TexturedModel> modelCache;

    public ModelCache() {
        this.modelCache = Collections.synchronizedMap(new HashMap<>());
    }

    public TexturedModel getByName(String name) {
        return modelCache.get(name);
    }

    public void addModel(String name, TexturedModel model) {
        modelCache.put(name, model);
    }
}
