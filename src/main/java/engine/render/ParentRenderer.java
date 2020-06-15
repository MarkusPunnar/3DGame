package engine.render;

import engine.DisplayManager;
import engine.font.GUIText;
import engine.font.structure.FontType;
import engine.font.structure.TextMeshData;
import engine.shader.Shader;
import game.object.item.Icon;
import game.ui.UIComponent;
import game.state.Game;
import game.state.State;
import game.object.Entity;
import game.object.Player;
import game.object.RenderObject;
import game.object.env.Camera;
import game.object.env.Light;
import game.object.terrain.Terrain;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL13;
import game.object.ObjectComparator;
import util.OpenGLUtil;

import java.io.IOException;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL30.*;

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
    private Collection<UIComponent> guis;
    private Collection<Icon> icons;
    private Map<FontType, List<GUIText>> texts;

    private Matrix4f projectionMatrix;

    public ParentRenderer() throws IOException {
        this.guis = new ArrayList<>();
        this.entityBatches = Collections.synchronizedSet(new TreeSet<>(new ObjectComparator()));
        this.terrains = new ArrayList<>();
        this.texts = new HashMap<>();
        this.icons = new ArrayList<>();
        this.guiRenderer = new GuiRenderer();
        this.fontRenderer = new FontRenderer();
    }

    public void load(Camera camera) throws IOException {
        OpenGLUtil.enableCulling();
        this.projectionMatrix = camera.createProjectionMatrix();
        initRenderers();
    }

    private void initRenderers() throws IOException {
        this.entityRenderer = new EntityRenderer(projectionMatrix);
        this.terrainRenderer = new TerrainRenderer(projectionMatrix);
        this.directionalShadowRenderer = new DirectionalShadowRenderer(projectionMatrix);
        this.pointShadowRenderer = new PointShadowRenderer(projectionMatrix);
    }

    public void processEntity(Entity entity) {
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

    public void processGuis(List<UIComponent> guis) {
        for (UIComponent gui : guis) {
            processGui(gui);
        }
    }

    private void processGui(UIComponent gui) {
        guis.add(gui);
    }

    public void processIcon(Icon icon) {
        icons.add(icon);
    }

    public void processEntities(List<Entity> entities) {
        for (Entity entity : entities) {
            processEntity(entity);
        }
    }

    public void renderObjects(List<Light> lights) {
        glViewport(0, 0, DisplayManager.getWidth(), DisplayManager.getHeight());
        prepare();
        if (Game.getInstance().getCurrentState() != State.IN_MENU) {
            bindDepthMaps(lights);
            doRender(entityRenderer, entityBatches, lights);
            doRender(terrainRenderer, terrains, lights);
        }
        doRender(guiRenderer, guis, lights);
        doRender(guiRenderer, icons, lights);
        for (FontType fontType : texts.keySet()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL13.glBindTexture(GL_TEXTURE_2D, fontType.getTextureAtlas());
            doRender(fontRenderer, texts.get(fontType), lights);
        }
    }

    public void updateDepthMaps(List<Light> lights, Player player) {
        glCullFace(GL_FRONT);
        for (Light light : lights) {
            glViewport(0, 0, light.getFbo().getShadowWidth(), light.getFbo().getShadowHeight());
            if (light.isActive(player)) {
                if (light.isPointLight()) {
                    renderPointDepthMap(light);
                } else {
                    renderDirectionalDepthMap(light);
                }
            }
            light.setInitialized(true);
        }
        glCullFace(GL_BACK);
    }

    private void renderDirectionalDepthMap(Light sun) {
        glBindFramebuffer(GL_FRAMEBUFFER, sun.getFbo().getFboID());
        glClear(GL_DEPTH_BUFFER_BIT);
        doRender(directionalShadowRenderer, entityBatches, List.of(sun));
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void renderPointDepthMap(Light pointLight) {
        glBindFramebuffer(GL_FRAMEBUFFER, pointLight.getFbo().getFboID());
        glClear(GL_DEPTH_BUFFER_BIT);
        doRender(pointShadowRenderer, entityBatches, List.of(pointLight));
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
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

    private void doRender(Renderer renderer, Collection<? extends RenderObject> objects, List<Light> lights) {
        Shader shader = renderer.getShader();
        shader.start();
        shader.loadUniforms(lights);
        renderer.render(objects);
        shader.stop();
    }


    private void prepare() {
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(RED, GREEN, BLUE, 1.0f);
    }

    public void cleanUp() {
        if (Game.getInstance().getCurrentState() != State.IN_MENU) {
            entityRenderer.getShader().cleanUp();
            terrainRenderer.getShader().cleanUp();
            guiRenderer.getShader().cleanUp();
            fontRenderer.getShader().cleanUp();
            directionalShadowRenderer.getShader().cleanUp();
            pointShadowRenderer.getShader().cleanUp();
        }
    }

    public void loadText(GUIText text) {
        FontType fontType = text.getFont();
        TextMeshData meshData = fontType.loadText(text);
        int vaoID = Game.getInstance().getLoader().loadToVAO(meshData.getVertices(), meshData.getTextureCoords());
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

    public void loadTexts(List<GUIText> guiTexts) {
        for (GUIText guiText : guiTexts) {
            loadText(guiText);
        }
    }

    public void removeText(GUIText text) {
        List<GUIText> fontTexts = texts.get(text.getFont());
        fontTexts.remove(text);
        if (fontTexts.isEmpty()) {
            texts.remove(text.getFont());
        }
    }

    public void removeTexts(List<GUIText> guiTexts) {
        for (GUIText guiText : guiTexts) {
            removeText(guiText);
        }
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Collection<UIComponent> getGuis() {
        return guis;
    }

    public Collection<Icon> getIcons() {
        return icons;
    }
}
