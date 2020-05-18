package util;

import object.RenderObject;
import util.math.structure.Triangle;

public class GeneratorUtil {

    public static void setParentObject(RenderObject object) {
        for (Triangle triangle : object.getModel().getRawModel().getTriangles()) {
            triangle.setParentObject(object);
        }
    }
}
