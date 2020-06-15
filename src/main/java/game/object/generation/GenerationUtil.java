package game.object.generation;

import engine.loader.VAOLoader;
import engine.texture.TextureCache;
import game.object.RenderObject;
import game.state.Game;
import org.joml.Vector2f;
import util.math.structure.Triangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenerationUtil {

    private static double TWO_PI = Math.PI * 2.0;
    private static double thetaAccuracy = 0.0001;

    public static void setParentObject(RenderObject object) {
        for (Triangle triangle : object.getTriangles()) {
            triangle.setParentObject(object);
        }
    }

    public static Vector2f fromOpenGLCoords(float x, float y) {
        return new Vector2f((1 + x) / 2f, Math.abs(y - 1) / 2f);
    }

    public static int getTextureFromCache(String textureName) throws IOException {
        VAOLoader loader = Game.getInstance().getLoader();
        TextureCache textureCache = Game.getInstance().getTextureCache();
        Integer textureID = textureCache.getByName(textureName);
        if (textureID == null) {
            textureID = loader.loadGuiTexture(textureName);
            textureCache.addTexture(textureName, textureID);
        }
        return textureID;
    }

    protected static List<Vector2f> generateEllipsePoints(int n, float a, float b) {
        List<Vector2f> ellipsePoints = new ArrayList<>();
        double theta = 0.0;
        double numIntegrals = Math.round(TWO_PI / thetaAccuracy);
        double circ = 0.0;
        double dpt;
        for (int i = 0; i < numIntegrals; i++) {
            theta += i * thetaAccuracy;
            dpt = computeDpt(a, b, theta);
            circ += dpt;
        }
        int nextPoint = 0;
        double run = 0.0;
        theta = 0.0;
        for (int i = 0; i < numIntegrals; i++) {
            theta += thetaAccuracy;
            double subIntegral = n * run / circ;
            if ((int) subIntegral >= nextPoint) {
                float x = (float) (a * Math.cos(theta));
                float y = (float) (b * Math.sin(theta));
                ellipsePoints.add(new Vector2f(x, y));
                nextPoint++;
            }
            run += computeDpt(a, b, theta);
        }
        return ellipsePoints;
    }

    private static double computeDpt(double r1, double r2, double theta) {
        double dpt_sin = Math.pow(r1 * Math.sin(theta), 2.0);
        double dpt_cos = Math.pow(r2 * Math.cos(theta), 2.0);
        return Math.sqrt(dpt_sin + dpt_cos);
    }

}
