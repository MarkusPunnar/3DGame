package game.ui.menu;

import java.util.HashMap;
import java.util.Map;

public class MenuCache {

    private Map<Integer, Menu> menuCache;

    public MenuCache() {
        this.menuCache = new HashMap<>();
    }

    public void addToCache(MenuType type, Menu menu) {
        menuCache.put(type.getMenuId(), menu);
    }

    public Menu getFromCache(MenuType type) {
        return menuCache.get(type.getMenuId());
    }
}
