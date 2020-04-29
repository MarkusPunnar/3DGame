package renderEngine;

import entity.Entity;
import entity.Player;
import entity.RenderObject;
import entity.env.Camera;
import entity.env.Light;
import entity.terrain.Terrain;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import shader.StaticShader;
import shader.TerrainShader;
import util.ObjectComparator;
import util.math.MathUtil;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class ParentRenderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 500;

    private static final float RED = 135f/256f;
    private static final float GREEN = 206f/256f;
    private static final float BLUE = 250f/256f;

    private Renderer entityRenderer;
    private Renderer terrainRenderer;

    private Collection<RenderObject> entityBatches;
    private Collection<RenderObject> terrains;

    private Matrix4f projectionMatrix;

    public ParentRenderer() throws IOException {
        enableCulling();
        createProjectionMatrix();
        this.entityRenderer = new EntityRenderer(new StaticShader(), projectionMatrix);
        this.terrainRenderer = new TerrainRenderer(new TerrainShader(), projectionMatrix);
        this.entityBatches = new TreeSet<>(new ObjectComparator());
        this.terrains = new ArrayList<>();
    }


    private void createProjectionMatrix() {
        long current = glfwGetCurrentContext();
        float aspectRatio;
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(current, pWidth, pHeight);
            aspectRatio = ((float) pWidth.get()) / ((float) pHeight.get());
        }
        projectionMatrix = new Matrix4f().perspective(((float) Math.toRadians(FOV)), aspectRatio, NEAR_PLANE, FAR_PLANE);
    }



    public void processEntity(Entity entity) {
        entityBatches.add(entity);
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void processEntities(List<Entity> entities, Player player) {
        for (Entity entity : entities) {
            processEntity(entity);
        }
        processEntity(player);
    }

    public void processTerrains(List<Terrain> terrains) {
        for (Terrain terrain : terrains) {
            processTerrain(terrain);
        }
    }

    public void renderObjects(Light sun, Camera camera) {
        prepare();
        doRender(entityRenderer, entityBatches, sun, camera);
        doRender(terrainRenderer, terrains, sun, camera);
        entityBatches.clear();
        terrains.clear();
    }

    private void doRender(Renderer renderer, Collection<RenderObject> objects, Light sun, Camera camera) {
        Shader shader = renderer.getShader();
        shader.start();
        shader.loadLight(sun);
        shader.loadSkyColour(RED, GREEN, BLUE);
        shader.loadCameraPosition(camera.getPosition());
        shader.doLoadMatrix(MathUtil.createViewMatrix(camera), "viewMatrix");
        renderer.render(objects);
        shader.stop();
    }


    private void prepare() {
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(RED, GREEN, BLUE, 1.0f);
    }

    public void cleanUp() {
        entityRenderer.getShader().cleanUp();
        terrainRenderer.getShader().cleanUp();
    }

    public static void enableCulling() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    public static void disableCulling() {
        glDisable(GL_CULL_FACE);
    }
}
