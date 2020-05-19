package engine.render;

import engine.font.GUIText;
import engine.font.structure.FontType;
import engine.font.structure.TextMeshData;
import engine.loader.VAOLoader;
import engine.shader.FontShader;
import engine.shader.Shader;
import object.Entity;
import object.Player;
import object.RenderObject;
import object.env.Camera;
import object.env.Light;
import object.terrain.Terrain;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryStack;
import engine.shader.StaticShader;
import engine.shader.TerrainShader;
import engine.texture.GuiTexture;
import util.GuiComparator;
import util.ObjectComparator;
import util.OpenGLUtil;
import util.math.MathUtil;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.*;

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
    private Renderer fontRenderer;

    private Collection<Entity> entityBatches;
    private Collection<Terrain> terrains;
    private Collection<GuiTexture> guis;
    private Map<FontType, List<GUIText>> texts;

    private Matrix4f projectionMatrix;
    private VAOLoader loader;

    public ParentRenderer(VAOLoader loader) throws IOException {
        OpenGLUtil.enableCulling();
        createProjectionMatrix();
        this.entityRenderer = new EntityRenderer(new StaticShader(), projectionMatrix);
        this.terrainRenderer = new TerrainRenderer(new TerrainShader(), projectionMatrix);
        this.guiRenderer = new GuiRenderer(loader);
        this.fontRenderer = new FontRenderer(new FontShader());
        this.entityBatches = new TreeSet<>(new ObjectComparator());
        this.terrains = new ArrayList<>();
        this.guis = new TreeSet<>(new GuiComparator());
        this.texts = new HashMap<>();
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

    public void processTerrains(List<Terrain> terrains) {
        for (Terrain terrain : terrains) {
            processTerrain(terrain);
        }
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

    public void renderObjects(List<Light> roomLights, List<Light> terrainLights, Camera camera) {
        prepare();
        doRender(entityRenderer, entityBatches, roomLights, camera);
        doRender(terrainRenderer, terrains, terrainLights, camera);
        doRender(guiRenderer, guis, null, null);
        for (FontType fontType : texts.keySet()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL13.glBindTexture(GL_TEXTURE_2D, fontType.getTextureAtlas());
            doRender(fontRenderer, texts.get(fontType), null, null);
        }
        entityBatches.clear();
        terrains.clear();
    }

    private void doRender(Renderer renderer, Collection<? extends RenderObject> objects, List<Light> lights, Camera camera) {
        Shader shader = renderer.getShader();
        shader.start();
        if (lights != null && camera != null) {
            shader.loadLights(lights, shader.getUniformLocations());
            shader.doLoadMatrix(MathUtil.createViewMatrix(camera), "viewMatrix");
        }
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
        fontRenderer.getShader().cleanUp();
    }

    public void loadText(GUIText text) {
        FontType fontType = text.getFont();
        TextMeshData meshData = fontType.loadText(text);
        int vaoID = loader.loadToVAO(meshData.getVertices(), meshData.getTextureCoords());
        text.setMeshInfo(vaoID, meshData.getVertexCount());
        if (texts.containsKey(fontType)) {
            List<GUIText> fontTexts = texts.get(fontType);
            fontTexts.add(text);
        } else {
            List<GUIText> fontTexts = new ArrayList<>();
            fontTexts.add(text);
            texts.put(fontType, fontTexts);
        }
    }

    public void removeText(GUIText text) {
        List<GUIText> fontTexts = texts.get(text.getFont());
        fontTexts.remove(text);
        if (fontTexts.isEmpty()) {
            texts.remove(text.getFont());
        }
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public VAOLoader getLoader() {
        return loader;
    }

    public Collection<GuiTexture> getGuis() {
        return guis;
    }
}
