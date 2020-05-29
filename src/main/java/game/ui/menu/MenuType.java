package game.ui.menu;

public enum MenuType {

    MAIN_MENU(0),
    OPTIONS_MENU(1),
    PAUSE_MENU(2),
    LOAD_MENU(3);

    private final int menuId;


    MenuType(int menuId) {
        this.menuId = menuId;
    }

    public int getMenuId() {
        return menuId;
    }
}
