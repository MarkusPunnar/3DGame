package engine.render;

import engine.DisplayManager;
import engine.font.GUIText;
import engine.font.structure.FontType;
import engine.font.structure.TextMeshData;
import engine.loader.VAOLoader;
import engine.shader.Shader;
import engine.shadow.ShadowFrameBuffer;
import engine.texture.GuiTexture;
import object.Entity;
import object.Player;
import object.RenderObject;
import object.env.Camera;
import object.env.Light;
import object.terrain.Terrain;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryStack;
import util.GuiComparator;
import util.ObjectComparator;
import util.OpenGLUtil;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class ParentRenderer {

    private static final float RED = 135f/256f;
    private static final float GREEN = 206f/256f;
    private static final float BLUE = 250f/256f;

    private Renderer entityRenderer;
    private Renderer terrainRenderer;
    private Renderer guiRenderer;
    private Renderer fontRenderer;
    private Renderer directionalShadowRenderer;
    private Renderer pointShadowRenderer;

    private Collection<Entity> entityBatches;
    private Collection<Terrain> terrains;
    private Collection<GuiTexture> guis;
    private Map<FontType, List<GUIText>> texts;

    private Matrix4f projectionMatrix;
    private VAOLoader loader;

    public ParentRenderer(VAOLoader loader, Camera camera) throws IOException {
        OpenGLUtil.enableCulling();
        this.projectionMatrix = camera.createProjectionMatrix();
        initRenderers(loader);
        this.entityBatches = new TreeSet<>(new ObjectComparator());
        this.terrains = new ArrayList<>();
        this.guis = new TreeSet<>(new GuiComparator());
        this.texts = new HashMap<>();
        this.loader = loader;
    }

    private void initRenderers(VAOLoader loader) throws IOException {
        this.entityRenderer = new EntityRenderer(projectionMatrix);
        this.terrainRenderer = new TerrainRenderer(projectionMatrix);
        this.guiRenderer = new GuiRenderer(loader);
        this.fontRenderer = new FontRenderer();
        this.directionalShadowRenderer = new DirectionalShadowRenderer(projectionMatrix);
        this.pointShadowRenderer = new PointShadowRenderer(projectionMatrix);
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

    public void renderObjects(List<Light> lights, Camera camera) {
        glViewport(0, 0, DisplayManager.getWidth(), DisplayManager.getHeight());
        doRenderScene(lights, camera);
        entityBatches.clear();
        terrains.clear();
    }

    public void updateDepthMaps(List<Light> lights, Player player, Camera camera) {
        glViewport(0, 0, ShadowFrameBuffer.SHADOW_WIDTH, ShadowFrameBuffer.SHADOW_HEIGHT);
        glCullFace(GL_FRONT);
        for (Light light : lights) {
            if (light.isActive(player)) {
                if (light.isPointLight()) {
                    renderPointDepthMap(light, camera);
                } else {
                    renderDirectionalDepthMap(light, camera);
                }
            }
            light.setInitialized(true);
        }
        glCullFace(GL_BACK);
    }

    private void renderDirectionalDepthMap(Light sun, Camera camera) {
        glBindFramebuffer(GL_FRAMEBUFFER, sun.getFbo().getFboID());
        glClear(GL_DEPTH_BUFFER_BIT);
        doRender(directionalShadowRenderer, entityBatches, List.of(sun), camera);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void renderPointDepthMap(Light pointLight, Camera camera) {
        glBindFramebuffer(GL_FRAMEBUFFER, pointLight.getFbo().getFboID());
        glClear(GL_DEPTH_BUFFER_BIT);
        doRender(pointShadowRenderer, entityBatches, List.of(pointLight), camera);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void doRenderScene(List<Light> lights, Camera camera) {
        prepare();
        bindDepthMaps(lights);
        doRender(entityRenderer, entityBatches, lights, camera);
        doRender(terrainRenderer, terrains, lights, camera);
        doRender(guiRenderer, guis, lights, camera);
        for (FontType fontType : texts.keySet()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL13.glBindTexture(GL_TEXTURE_2D, fontType.getTextureAtlas());
            doRender(fontRenderer, texts.get(fontType), lights, camera);
        }
    }

    private void bindDepthMaps(List<Light> lights) {
        for (int i = 0; i < lights.size(); i++) {
            GL13.glActiveTexture(GL_TEXTURE5 + i);
            int depthMapTextureID = lights.get(i).getFbo().getDepthMapTextureID();
            if (lights.get(i).isPointLight()) {
                glBindTexture(GL_TEXTURE_CUBE_MAP, depthMapTextureID);
            } else {
                glBindTexture(GL_TEXTURE_2D, depthMapTextureID);
            }
        }
    }

    private void doRender(Renderer renderer, Collection<? extends RenderObject> objects, List<Light> lights, Camera camera) {
        Shader shader = renderer.getShader();
        shader.start();
        shader.loadUniforms(lights, camera);
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
        directionalShadowRenderer.getShader().cleanUp();
        pointShadowRenderer.getShader().cleanUp();
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
