package util;

import engine.render.RenderObject;

import java.util.Comparator;

public class GuiComparator implements Comparator<RenderObject> {

    @Override
    public int compare(RenderObject type, RenderObject otherType) {
        int priorityDiff = type.getPriority() - otherType.getPriority();
        if (priorityDiff == 0) {
            boolean equal = type.equals(otherType);
            priorityDiff += equal ? 0 : -1;
        }
        return priorityDiff;
    }
}
