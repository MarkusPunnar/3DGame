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
import engine.model.TexturedModel;
import engine.model.data.ModelData;
import org.joml.Vector3f;
import engine.loader.VAOLoader;
import engine.texture.ModelTexture;
import util.GeneratorUtil;

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
        TexturedModel tavernModel = GeneratorUtil.getTexturedModel(loader, "tavern");
        ModelData roomBoxData = ObjectLoader.loadObjectModel("hitbox/tavernbox");
        tavernModel.getRawModel().setTriangles(loader.createTriangles(roomBoxData.getVertices(), roomBoxData.getIndices()));
        tavernModel.getTexture().isTransparent(true);
        Entity tavern = new Entity.Builder(tavernModel, new Vector3f()).build();
        roomEntities.add(tavern);
        //Generate stools
        TexturedModel stoolModel = GeneratorUtil.getTexturedModel(loader, "stool");
        for (int i = 0; i < 3; i++) {
            roomEntities.add(new Entity.Builder(stoolModel, new Vector3f(3 + 12 * i, 0, 25)).build());
        }
        for (int i = 0; i < 4; i++) {
            roomEntities.add(new Entity.Builder(stoolModel, new Vector3f(-13, 0, 8 - 15 * i)).build());
        }
        //Generate barrels
        TexturedModel barrelModel = GeneratorUtil.getTexturedModel(loader, "barrel");
        roomEntities.add(new Entity.Builder(barrelModel, new Vector3f(3, 0, 10)).scale(new Vector3f(5)).build());
        roomEntities.add(new Entity.Builder(barrelModel, new Vector3f(28, 0, -62)).scale(new Vector3f(5)).build());
        roomEntities.add(new Entity.Builder(barrelModel, new Vector3f(30, 0, -33)).scale(new Vector3f(5)).build());
        //Generate tables and chairs
        TexturedModel tableModel = GeneratorUtil.getTexturedModel(loader, "table");
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
        //Generate cabinets
        roomEntities.addAll(generateCabinets());
        //Generate shelves
        roomEntities.addAll(generateShelves());
        return roomEntities;
    }

    private List<Entity> generateShelves() throws IOException, URISyntaxException {
        List<Entity> shelves = new ArrayList<>();
        TexturedModel shelfModel = GeneratorUtil.getTexturedModel(loader, "shelf");
        shelves.add(new Entity.Builder(shelfModel, new Vector3f(33, 20, 6)).scale(new Vector3f(3)).rotationY(-90).build());
        shelves.add(new Entity.Builder(shelfModel, new Vector3f(33, 20, -13)).scale(new Vector3f(3)).rotationY(-90).build());
        shelves.add(new Entity.Builder(shelfModel, new Vector3f(33, 20, -32)).scale(new Vector3f(3)).rotationY(-90).build());
        return shelves;
    }

    private List<Entity> generateCabinets() throws IOException, URISyntaxException {
        List<Entity> cabinets = new ArrayList<>();
        TexturedModel cabinetModel = GeneratorUtil.getTexturedModel(loader, "cabinet");
        cabinets.add(new Entity.Builder(cabinetModel, new Vector3f(30, 0, 3)).scale(new Vector3f(3)).rotationY(-90).build());
        cabinets.add(new Entity.Builder(cabinetModel, new Vector3f(30, 0, -17)).scale(new Vector3f(3)).rotationY(-90).build());
        return cabinets;
    }

    private List<Entity> generateLanterns(List<Light> lights) throws IOException, URISyntaxException {
        List<Entity> lanterns = new ArrayList<>();
        TexturedModel lanternModel = GeneratorUtil.getTexturedModel(loader, "lantern");
        Vector3f lanternAttenuation = new Vector3f(0.8f, 0.0005f, 0.0001f);
        Vector3f lanternLightColour = new Vector3f(189f / 255f, 183f / 255f, 107f / 255f);
        //Room lanterns
        LightEntity lantern = new LightEntity.Builder(lanternModel, new Vector3f(31, 48.5f, 50)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .offset(new Vector3f(-5, 7, 0)).regionMin(new Vector3f(-40, 34.5f, 27))
                .regionMax(new Vector3f(31, 67, 70)).pointLight(true).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        lantern = new LightEntity.Builder(lanternModel, new Vector3f(31, 48.5f, 3.5f)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .offset(new Vector3f(-5, 7, 0)).regionMin(new Vector3f(-40, 34.5f, -20))
                .regionMax(new Vector3f(31, 67, 22)).pointLight(true).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        lantern = new LightEntity.Builder(lanternModel, new Vector3f(31, 48.5f, -43)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .offset(new Vector3f(-5, 7, 0)).regionMin(new Vector3f(-40, 34.5f, -65))
                .regionMax(new Vector3f(31, 67, -25)).pointLight(true).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        //Second floor lanterns
        lantern = new LightEntity.Builder(lanternModel, new Vector3f(-42.5f, 55, 95)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .offsetX(7).regionMin(new Vector3f(-40, 34.5f, 27)).regionMax(new Vector3f(92, 67, 121)).pointLight(true).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        lantern = new LightEntity.Builder(lanternModel, new Vector3f(93, 55, 95)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .offsetX(-7).regionMin(new Vector3f(0, 0, 67)).regionMax(new Vector3f(new Vector3f(92, 67, 121))).pointLight(true).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        //First floor lanterns
        lantern = new LightEntity.Builder(lanternModel, new Vector3f(-7, 20, 123.5f)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .regionMin(new Vector3f(-40, -1, 27)).regionMax(new Vector3f(40, 30, 121)).offsetZ(-5).pointLight(true).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        lantern = new LightEntity.Builder(lanternModel, new Vector3f(-42, 20, -5)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .regionMin(new Vector3f(-40, -1, -65)).regionMax(new Vector3f(31, 30, 62)).offsetX(5).pointLight(true).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        return lanterns;
    }

    private List<Entity> generateNightstands() throws IOException, URISyntaxException {
        List<Entity> nightstands = new ArrayList<>();
        TexturedModel nightstandModel = GeneratorUtil.getTexturedModel(loader, "nightstand");
        for (int i = 0; i < 3; i++) {
            nightstands.add(new Entity.Builder(nightstandModel, new Vector3f(31, 34.5f, 50 - 46.5f * i)).rotationY(-90).scale(new Vector3f(20)).build());
        }
        return nightstands;
    }

    private List<Entity> generateChests() throws IOException, URISyntaxException {
        List<Entity> chests = new ArrayList<>();
        TexturedModel openChestModel = GeneratorUtil.getTexturedModel(loader, "openchest");
        TexturedModel closedChestModel = GeneratorUtil.getTexturedModel(loader, "closedchest");
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
        TexturedModel bedModel = GeneratorUtil.getTexturedModel(loader, "bed");
        ModelData bedModelData = ObjectLoader.loadObjectModel("hitbox/bedbox");
        bedModel.getRawModel().setTriangles(loader.createTriangles(bedModelData.getVertices(), bedModelData.getIndices()));
        for (int i = 0; i < 3; i++) {
            beds.add(new Entity.Builder(bedModel, new Vector3f(20, 34.5f, 31 - 46.5f * i)).build());
        }
        return beds;
    }

    private List<Entity> generateDoors() throws IOException, URISyntaxException {
        List<Entity> doors = new ArrayList<>();
        TexturedModel doorModel = GeneratorUtil.getTexturedModel(loader, "door");
        Door door = new Door.Builder(doorModel, new Vector3f(-44, 0, -26.5f)).facing(FacingDirection.WEST).build();
        HandlerState.getInstance().registerInteractableEntity(door);
        doors.add(door);
        for (int i = 0; i < 3; i++) {
            door = new Door.Builder(doorModel, new Vector3f(-13.7f, 34.5f, 54.3f - 46.2f * i)).facing(FacingDirection.EAST).build();
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

    public Player generatePlayer() throws IOException, URISyntaxException {
        ModelTexture purpleTexture = new ModelTexture(loader.loadObjectTexture("purple"));
        TexturedModel playerModel = GeneratorUtil.getTexturedModel(loader, "player", purpleTexture);
        return new Player.Builder(playerModel, new Vector3f(-25, 34.5f, -50)).scale(new Vector3f(3)).build();
    }
}
