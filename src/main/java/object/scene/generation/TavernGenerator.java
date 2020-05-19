package object.scene.generation;

import game.state.HandlerState;
import object.Entity;
import object.env.Light;
import object.env.LightEntity;
import object.item.Coin;
import object.scene.Chest;
import object.scene.Door;
import util.FacingDirection;
import engine.loader.ObjectLoader;
import object.Player;
import engine.model.RawModel;
import engine.model.TexturedModel;
import engine.model.data.ModelData;
import org.joml.Vector3f;
import engine.loader.VAOLoader;
import engine.texture.ModelTexture;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class TavernGenerator implements Generator {

    private VAOLoader loader;

    public TavernGenerator(VAOLoader loader) {
        this.loader = loader;
    }

    public List<Entity> generate(List<Light> lights) throws IOException, URISyntaxException {
        List<Entity> roomEntities = new ArrayList<>();
        //Generate room background
        TexturedModel tavernModel = getTexturedModel("tavern", false);
        ModelData roomBoxData = ObjectLoader.loadObjectModel("hitbox/tavernbox");
        tavernModel.getRawModel().setTriangles(loader.createTriangles(roomBoxData.getVertices(), roomBoxData.getIndices()));
        tavernModel.getTexture().isTransparent(true);
        Entity tavern = new Entity.Builder(tavernModel, new Vector3f()).build();
        roomEntities.add(tavern);
        //Generate stools
        TexturedModel stoolModel = getTexturedModel("stool", false);
        for (int i = 0; i < 3; i++) {
            roomEntities.add(new Entity.Builder(stoolModel, new Vector3f(3 + 12 * i, 0, 25)).build());
        }
        for (int i = 0; i < 4; i++) {
            roomEntities.add(new Entity.Builder(stoolModel, new Vector3f(-13, 0, 8 - 15 * i)).build());
        }
        //Generate barrels
        TexturedModel barrelModel = getTexturedModel("barrel", false);
        roomEntities.add(new Entity.Builder(barrelModel, new Vector3f(3, 0, 10)).scale(new Vector3f(5)).build());
        roomEntities.add(new Entity.Builder(barrelModel, new Vector3f(28, 0, -62)).scale(new Vector3f(5)).build());
        roomEntities.add(new Entity.Builder(barrelModel, new Vector3f(30, 0, -33)).scale(new Vector3f(5)).build());
        //Generate tables and chairs
        TexturedModel tableModel = getTexturedModel("table", false);
        roomEntities.addAll(generateTableWithChairs(tableModel, stoolModel, new Vector3f(-27, 1.5f, 100)));
        roomEntities.addAll(generateTableWithChairs(tableModel, stoolModel, new Vector3f(15, 1.5f, 103)));
        //Generate doors
        roomEntities.addAll(generateDoors());
        //Generate beds
        roomEntities.addAll(generateBeds());
        //Generate chests
        roomEntities.addAll(generateChests());
        //Generate nightstands
        roomEntities.addAll(generateNightstands());
        //Generate lanterns
        roomEntities.addAll(generateLanterns(lights));
        return roomEntities;
    }

    private List<Entity> generateLanterns(List<Light> lights) throws IOException, URISyntaxException {
        List<Entity> lanterns = new ArrayList<>();
        TexturedModel lanternModel = getTexturedModel("lantern", false);
        for (int i = 0; i < 3; i++) {
            lanterns.add(generateLantern(lanternModel, new Vector3f(31, 48.5f, 50 - 46.5f * i), lights));
        }
        lanterns.add(generateLantern(lanternModel, new Vector3f(0, 55, 124), lights));
        lanterns.add(generateLantern(lanternModel, new Vector3f(-42, 55, 0), lights));
        lanterns.add(generateLantern(lanternModel, new Vector3f(15, 14, 103), lights));
        lanterns.add(generateLantern(lanternModel, new Vector3f(-27, 14, 100), lights));
        lanterns.add(generateLantern(lanternModel, new Vector3f(-5, 14.5f, 0), lights));
        return lanterns;
    }

    private LightEntity generateLantern(TexturedModel lanternModel, Vector3f position, List<Light> lights) {
        Vector3f lanternAttenuation = new Vector3f(1, 0.0005f, 0.0003f);
        Vector3f lanternLightColour = new Vector3f(189f / 255f, 183f / 255f, 107f / 255f);
        LightEntity lantern = new LightEntity.Builder(lanternModel, position).colour(lanternLightColour).attenuation(lanternAttenuation).build();
        lights.add(lantern.getLight());
        return lantern;
    }

    private List<Entity> generateNightstands() throws IOException, URISyntaxException {
        List<Entity> nightstands = new ArrayList<>();
        TexturedModel nightstandModel = getTexturedModel("nightstand", false);
        for (int i = 0; i < 3; i++) {
            nightstands.add(new Entity.Builder(nightstandModel, new Vector3f(31, 34.5f, 50 - 46.5f * i)).rotationY(-90).scale(new Vector3f(20)).build());
        }
        return nightstands;
    }

    private List<Entity> generateChests() throws IOException, URISyntaxException {
        List<Entity> chests = new ArrayList<>();
        TexturedModel openChestModel = getTexturedModel("openchest", false);
        TexturedModel closedChestModel = getTexturedModel("closedchest", false);
        for (int i = 0; i < 3; i++) {
            Chest chest = new Chest.Builder(closedChestModel, new Vector3f(-5, 34.5f, 31 - 46.5f * i), openChestModel).capacity(20).build();
            chest.addItem(new Coin(loader, 10));
            chests.add(chest);
            HandlerState.getInstance().registerInteractableEntity(chest);
        }
        return chests;
    }

    private List<Entity> generateBeds() throws IOException, URISyntaxException {
        List<Entity> beds = new ArrayList<>();
        TexturedModel bedModel = getTexturedModel("bed", false);
        ModelData bedModelData = ObjectLoader.loadObjectModel("hitbox/bedbox");
        bedModel.getRawModel().setTriangles(loader.createTriangles(bedModelData.getVertices(), bedModelData.getIndices()));
        for (int i = 0; i < 3; i++) {
            beds.add(new Entity.Builder(bedModel, new Vector3f(20, 34.5f, 31 - 46.5f * i)).build());
        }
        return beds;
    }

    private List<Entity> generateDoors() throws IOException, URISyntaxException {
        List<Entity> doors = new ArrayList<>();
        TexturedModel doorModel = getTexturedModel("door", false);
        Door door = new Door.Builder(doorModel, new Vector3f(-44, 0, -26.5f)).facing(FacingDirection.WEST).build();
        HandlerState.getInstance().registerInteractableEntity(door);
        doors.add(door);
        for (int i = 0; i < 3; i++) {
            door = new Door.Builder(doorModel, new Vector3f(-13.7f, 34.5f, 54.3f - 46.5f * i)).facing(FacingDirection.EAST).build();
            HandlerState.getInstance().registerInteractableEntity(door);
            doors.add(door);
        }
        return doors;
    }

    private List<Entity> generateTableWithChairs(TexturedModel tableModel, TexturedModel stoolModel, Vector3f tablePosition) {
        List<Entity> tables = new ArrayList<>();
        tables.add(new Entity.Builder(tableModel, tablePosition).scale(new Vector3f(0.75f)).build());
        tables.add(new Entity.Builder(stoolModel, new Vector3f(tablePosition.x + 12, 1, tablePosition.z - 13)).scaleY(0.8f).build());
        tables.add(new Entity.Builder(stoolModel, new Vector3f(tablePosition.x - 10, 1, tablePosition.z - 13)).scaleY(0.8f).build());
        tables.add(new Entity.Builder(stoolModel, new Vector3f(tablePosition.x - 10, 1, tablePosition.z + 9)).scaleY(0.8f).build());
        tables.add(new Entity.Builder(stoolModel, new Vector3f(tablePosition.x + 12, 1, tablePosition.z + 9)).scaleY(0.8f).build());
        return tables;
    }

    public Player generatePlayer(VAOLoader loader) throws IOException, URISyntaxException {
        ModelTexture purpleTexture = new ModelTexture(loader.loadObjectTexture("purple"));
        TexturedModel playerModel = getTexturedModel("player", purpleTexture);
        return new Player.Builder(playerModel, new Vector3f(-30, 34.5f, -25)).scale(new Vector3f(3)).build();
    }

    private TexturedModel getTexturedModel(String objName, ModelTexture texture) throws IOException, URISyntaxException {
        ModelData modelData = ObjectLoader.loadObjectModel(objName);
        RawModel rawModel = loader.loadToVAO(modelData.getVertices(), modelData.getIndices(), modelData.getNormals(), modelData.getTextureCoords());
        return new TexturedModel(rawModel, texture);
    }

    private TexturedModel getTexturedModel(String fileName, boolean setTransparent) throws URISyntaxException, IOException {
        ModelData modelData = ObjectLoader.loadObjectModel(fileName);
        RawModel rawModel = loader.loadToVAO(modelData.getVertices(), modelData.getIndices(), modelData.getNormals(), modelData.getTextureCoords());
        ModelTexture texture = new ModelTexture(loader.loadObjectTexture(fileName));
        texture.isTransparent(setTransparent);
        return new TexturedModel(rawModel, texture);
    }
}
