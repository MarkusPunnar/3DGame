package util;

import engine.texture.GuiTexture;

import java.util.Comparator;

public class GuiComparator implements Comparator<GuiTexture> {

    @Override
    public int compare(GuiTexture type, GuiTexture otherType) {
        return type.getPriority() - otherType.getPriority();
    }
}
