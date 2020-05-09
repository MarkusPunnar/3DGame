package engine.render;

import engine.loader.Loader;
import object.Entity;
import object.Player;
import object.env.Camera;
import object.env.Light;
import object.item.Slot;
import object.terrain.Terrain;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import engine.shader.Shader;
import engine.shader.StaticShader;
import engine.shader.TerrainShader;
import engine.texture.GuiTexture;
import util.GuiComparator;
import util.ObjectComparator;
import util.OpenGLUtil;
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
    private Renderer guiRenderer;

    private Collection<RenderObject> entityBatches;
    private Collection<RenderObject> terrains;
    private Collection<RenderObject> guis;

    private Matrix4f projectionMatrix;
    private Loader loader;

    public ParentRenderer(Loader loader) throws IOException {
        OpenGLUtil.enableCulling();
        createProjectionMatrix();
        this.entityRenderer = new EntityRenderer(new StaticShader(), projectionMatrix);
        this.terrainRenderer = new TerrainRenderer(new TerrainShader(), projectionMatrix);
        this.guiRenderer = new GuiRenderer(loader);
        this.entityBatches = new TreeSet<>(new ObjectComparator());
        this.terrains = new ArrayList<>();
        this.guis = new TreeSet<>(new GuiComparator());
        this.loader = loader;
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



    private void processEntity(Entity entity) {
        entityBatches.add(entity);
    }

    private void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void processGui(GuiTexture gui) {
        guis.add(gui);
    }

    public void processEntities(List<Entity> entities, Player player) {
        for (Entity entity : entities) {
            processEntity(entity);
        }
        processEntity(player);
    }

    public void renderObjects(Light sun, Camera camera) {
        prepare();
        doRender(entityRenderer, entityBatches, sun, camera);
        doRender(terrainRenderer, terrains, sun, camera);
        doRender(guiRenderer, guis, null, null);
        entityBatches.clear();
        terrains.clear();
    }

    private void doRender(Renderer renderer, Collection<RenderObject> objects, Light sun, Camera camera) {
        Shader shader = renderer.getShader();
        shader.start();
        if (sun != null && camera != null) {
            shader.loadLight(sun);
            shader.loadCameraPosition(camera.getPosition());
            shader.doLoadMatrix(MathUtil.createViewMatrix(camera), "viewMatrix");
        }
        shader.loadSkyColour(RED, GREEN, BLUE);
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
        guiRenderer.getShader().cleanUp();
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Loader getLoader() {
        return loader;
    }

    public Collection<RenderObject> getGuis() {
        return guis;
    }
}
