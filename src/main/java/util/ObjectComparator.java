package util;

import renderEngine.RenderObject;

import java.util.Comparator;

public class ObjectComparator implements Comparator<RenderObject> {
    @Override
    public int compare(RenderObject renderObject, RenderObject other) {
        int idDiff = renderObject.getID() - other.getID();
        if (idDiff == 0) {
            boolean equal = renderObject.equals(other);
            idDiff += equal ? 0 : 1;
        }
        return idDiff;
    }
}
