package game.object.generation;

import com.google.common.flogger.FluentLogger;
import game.state.Game;
import game.state.HandlerState;
import game.object.Entity;
import game.object.env.Light;
import game.object.env.LightEntity;
import game.object.item.Coin;
import game.object.scene.Chest;
import game.object.scene.Door;
import util.FacingDirection;
import engine.loader.ObjectLoader;
import game.object.Player;
import engine.model.TexturedModel;
import engine.model.data.ModelData;
import org.joml.Vector3f;
import engine.model.ModelTexture;
import util.GeneratorUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TavernGenerator implements Generator {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public List<Entity> generate() throws IOException {
        List<Entity> roomEntities = new ArrayList<>();
        //Generate room background
        TexturedModel tavernModel = GeneratorUtil.getTexturedModel("tavern");
        ModelData roomBoxData = ObjectLoader.loadObjectModel("hitbox/tavernbox");
        tavernModel.getTexture().isTransparent(true);
        Entity tavern = new Entity.Builder(tavernModel, new Vector3f()).build();
        tavern.setTriangles(Game.getInstance().getLoader().createTriangles(roomBoxData.getVertices(), roomBoxData.getIndices()));
        GeneratorUtil.setParentObject(tavern);
        roomEntities.add(tavern);
        //Generate stools
        TexturedModel stoolModel = GeneratorUtil.getTexturedModel("stool");
        for (int i = 0; i < 3; i++) {
            roomEntities.add(new Entity.Builder(stoolModel, new Vector3f(3 + 12 * i, 0, 25)).build());
        }
        for (int i = 0; i < 4; i++) {
            roomEntities.add(new Entity.Builder(stoolModel, new Vector3f(-13, 0, 8 - 15 * i)).build());
        }
        //Generate barrels
        TexturedModel barrelModel = GeneratorUtil.getTexturedModel("barrel");
        roomEntities.add(new Entity.Builder(barrelModel, new Vector3f(3, 0, 10)).scale(new Vector3f(5)).build());
        roomEntities.add(new Entity.Builder(barrelModel, new Vector3f(28, 0, -62)).scale(new Vector3f(5)).build());
        roomEntities.add(new Entity.Builder(barrelModel, new Vector3f(30, 0, -33)).scale(new Vector3f(5)).build());
        //Generate tables and chairs
        TexturedModel tableModel = GeneratorUtil.getTexturedModel("table");
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
        roomEntities.addAll(generateLanterns(Game.getInstance().getActiveLights()));
        //Generate cabinets
        roomEntities.addAll(generateCabinets());
        //Generate shelves
        roomEntities.addAll(generateShelves());
        logger.atInfo().log("%s - Entities generated", getClass().getSimpleName());
        return roomEntities;
    }

    private List<Entity> generateShelves() throws IOException {
        List<Entity> shelves = new ArrayList<>();
        TexturedModel shelfModel = GeneratorUtil.getTexturedModel("shelf");
        shelves.add(new Entity.Builder(shelfModel, new Vector3f(33, 20, 6)).scale(new Vector3f(3)).rotationY(-90).build());
        shelves.add(new Entity.Builder(shelfModel, new Vector3f(33, 20, -13)).scale(new Vector3f(3)).rotationY(-90).build());
        shelves.add(new Entity.Builder(shelfModel, new Vector3f(33, 20, -32)).scale(new Vector3f(3)).rotationY(-90).build());
        return shelves;
    }

    private List<Entity> generateCabinets() throws IOException {
        List<Entity> cabinets = new ArrayList<>();
        TexturedModel cabinetModel = GeneratorUtil.getTexturedModel("cabinet");
        cabinets.add(new Entity.Builder(cabinetModel, new Vector3f(30, 0, 3)).scale(new Vector3f(3)).rotationY(-90).build());
        cabinets.add(new Entity.Builder(cabinetModel, new Vector3f(30, 0, -17)).scale(new Vector3f(3)).rotationY(-90).build());
        return cabinets;
    }

    private List<Entity> generateLanterns(List<Light> lights) throws IOException {
        List<Entity> lanterns = new ArrayList<>();
        TexturedModel lanternModel = GeneratorUtil.getTexturedModel("lantern");
        Vector3f lanternAttenuation = new Vector3f(0.8f, 0.0005f, 0.0001f);
        Vector3f lanternLightColour = new Vector3f(200f / 255f, 165f / 255f, 0 / 255f);
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
                .regionMin(new Vector3f(-50, -1, -65)).regionMax(new Vector3f(31, 30, 62)).offsetX(5).pointLight(true).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        return lanterns;
    }

    private List<Entity> generateNightstands() throws IOException {
        List<Entity> nightstands = new ArrayList<>();
        TexturedModel nightstandModel = GeneratorUtil.getTexturedModel("nightstand");
        for (int i = 0; i < 3; i++) {
            nightstands.add(new Entity.Builder(nightstandModel, new Vector3f(31, 34.5f, 50 - 46.5f * i)).rotationY(-90).scale(new Vector3f(20)).build());
        }
        return nightstands;
    }

    private List<Entity> generateChests() throws IOException {
        List<Entity> chests = new ArrayList<>();
        TexturedModel openChestModel = GeneratorUtil.getTexturedModel("openchest");
        TexturedModel closedChestModel = GeneratorUtil.getTexturedModel("closedchest");
        for (int i = 0; i < 3; i++) {
            Chest chest = new Chest.Builder(closedChestModel, new Vector3f(-5, 34.5f, 31 - 46.5f * i), openChestModel).capacity(18).build();
            chest.addItem(new Coin(10));
            chests.add(chest);
            HandlerState.getInstance().registerInteractableEntity(chest);
        }
        return chests;
    }

    private List<Entity> generateBeds() throws IOException {
        List<Entity> beds = new ArrayList<>();
        TexturedModel bedModel = GeneratorUtil.getTexturedModel("bed");
        ModelData bedModelData = ObjectLoader.loadObjectModel("hitbox/bedbox");
        for (int i = 0; i < 3; i++) {
            Entity bed = new Entity.Builder(bedModel, new Vector3f(20, 34.5f, 31 - 46.5f * i)).build();
            bed.setTriangles(Game.getInstance().getLoader().createTriangles(bedModelData.getVertices(), bedModelData.getIndices()));
            GeneratorUtil.setParentObject(bed);
            beds.add(bed);
        }
        return beds;
    }

    private List<Entity> generateDoors() throws IOException {
        List<Entity> doors = new ArrayList<>();
        TexturedModel doorModel = GeneratorUtil.getTexturedModel( "door");
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

    public Player generatePlayer() throws IOException {
        ModelTexture purpleTexture = new ModelTexture(Game.getInstance().getLoader().loadObjectTexture("purple"));
        TexturedModel playerModel = GeneratorUtil.getTexturedModel("player", purpleTexture);
        return new Player.Builder(playerModel, new Vector3f(-25, 34.5f, -50)).scale(new Vector3f(3)).build();
    }
}
