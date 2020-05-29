package util;

import game.ui.UIComponent;

import java.util.Comparator;

public class GuiComparator implements Comparator<UIComponent> {

    @Override
    public int compare(UIComponent type, UIComponent otherType) {
        return type.getPriority() - otherType.getPriority();
    }
}
