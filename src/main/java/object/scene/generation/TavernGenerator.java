package object.scene.generation;

import game.state.GameState;
import object.Entity;
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
import engine.loader.Loader;
import engine.texture.ModelTexture;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class TavernGenerator implements Generator {

    private Loader loader;
    private GameState gameState;

    public TavernGenerator(Loader loader, GameState state) {
        this.loader = loader;
        this.gameState = state;
    }

    public List<Entity> generate() throws IOException, URISyntaxException {
        List<Entity> roomEntities = new ArrayList<>();
        //Generate room background
        TexturedModel roomModel = getTexturedModel("room", false);
        Entity room = new Entity(roomModel, new Vector3f(), new Vector3f(), new Vector3f(1));
        ModelData roomBoxData = ObjectLoader.loadObjectModel("roombox");
        room.getModel().getRawModel().setTriangles(loader.createTriangles(roomBoxData.getVertices(), roomBoxData.getIndices()));
        room.getModel().getTexture().isTransparent(true);
        roomEntities.add(room);
        //Generate stools
        TexturedModel stoolModel = getTexturedModel("stool", false);
        for (int i = 0; i < 3; i++) {
            roomEntities.add(new Entity(stoolModel, new Vector3f(3 + 12 * i, 0, 25), new Vector3f(), new Vector3f(1)));
        }
        for (int i = 0; i < 4; i++) {
            roomEntities.add(new Entity(stoolModel, new Vector3f(-13, 0,8 - 15 * i), new Vector3f(), new Vector3f(1)));
        }
        //Generate barrels
        TexturedModel barrelModel = getTexturedModel("barrel", false);
        roomEntities.add(new Entity(barrelModel, new Vector3f(3,0,10), new Vector3f(), new Vector3f(5)));
        roomEntities.add(new Entity(barrelModel, new Vector3f(28,0,-62),new Vector3f(), new Vector3f(5)));
        roomEntities.add(new Entity(barrelModel, new Vector3f(30,0,-33),new Vector3f(), new Vector3f(5)));
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
        return roomEntities;
    }

    private List<Entity> generateNightstands() throws IOException, URISyntaxException {
        List<Entity> nightstands = new ArrayList<>();
        TexturedModel nightstandModel = getTexturedModel("nightstand", false);
        for (int i = 0; i < 3; i++) {
            nightstands.add(new Entity(nightstandModel, new Vector3f(31, 34.5f, 50 - 46.5f * i), new Vector3f(0, -90, 0), new Vector3f(20)));
        }
        return nightstands;
    }

    private List<Entity> generateChests() throws IOException, URISyntaxException {
        List<Entity> chests = new ArrayList<>();
        TexturedModel openChestModel = getTexturedModel("openchest", false);
        TexturedModel closedChestModel = getTexturedModel("closedchest", false);
        for (int i = 0; i < 3; i++) {
            Chest chest = new Chest(closedChestModel, new Vector3f(-5, 34.5f, 31 - 46.5f * i), new Vector3f(), new Vector3f(1), openChestModel, closedChestModel);
            chest.addItem(new Coin(loader, 10));
            chests.add(chest);
            gameState.getHandlerState().registerInteractableObject(chest);
        }
        return chests;
    }

    private List<Entity> generateBeds() throws IOException, URISyntaxException {
        List<Entity> beds = new ArrayList<>();
        TexturedModel bedModel = getTexturedModel("bed", false);
        for (int i = 0; i < 3; i++) {
            beds.add(new Entity(bedModel, new Vector3f(20, 34.5f, 31 - 46.5f * i), new Vector3f(), new Vector3f(1)));
        }
        return beds;
    }

    private List<Entity> generateDoors() throws IOException, URISyntaxException {
        List<Entity> doors = new ArrayList<>();
        TexturedModel doorModel = getTexturedModel("door", false);
        Door door = new Door(doorModel, new Vector3f(-44, 0, -26.5f), new Vector3f(), new Vector3f(1), FacingDirection.WEST);
        gameState.getHandlerState().registerInteractableObject(door);
        doors.add(door);
        for (int i = 0; i < 3; i++) {
            door = new Door(doorModel, new Vector3f(-13.7f, 34.5f, 54.3f - 46.5f * i), new Vector3f(), new Vector3f(1), FacingDirection.EAST);
            gameState.getHandlerState().registerInteractableObject(door);
            doors.add(door);
        }
        return doors;
    }

    private List<Entity> generateTableWithChairs(TexturedModel tableModel, TexturedModel stoolModel, Vector3f tablePosition) {
        List<Entity> tables = new ArrayList<>();
        tables.add(new Entity(tableModel, tablePosition, new Vector3f(), new Vector3f(0.75f)));
        tables.add(new Entity(stoolModel, new Vector3f(tablePosition.x + 12, 1, tablePosition.z - 13), new Vector3f(), new Vector3f(1, 0.8f, 1)));
        tables.add(new Entity(stoolModel, new Vector3f(tablePosition.x - 10, 1, tablePosition.z - 13), new Vector3f(), new Vector3f(1, 0.8f, 1)));
        tables.add(new Entity(stoolModel, new Vector3f(tablePosition.x - 10, 1, tablePosition.z + 9), new Vector3f(), new Vector3f(1, 0.8f, 1)));
        tables.add(new Entity(stoolModel, new Vector3f(tablePosition.x + 12, 1, tablePosition.z + 9), new Vector3f(), new Vector3f(1, 0.8f, 1)));
        return tables;
    }

    public Player generatePlayer(Loader loader) throws IOException, URISyntaxException {
        ModelTexture purpleTexture = new ModelTexture(loader.loadObjectTexture("purple"));
        TexturedModel playerModel = getTexturedModel("player", purpleTexture);
        return new Player(playerModel, new Vector3f(-30,34.5f,-25),new Vector3f(), new Vector3f(3));
    }

    private TexturedModel getTexturedModel(String objName, ModelTexture texture) throws IOException, URISyntaxException {
        ModelData modelData = ObjectLoader.loadObjectModel(objName);
        RawModel rawModel = loader.loadToVAO(modelData.getVertices(), modelData.getIndices(), modelData.getNormals(), modelData.getTextureCoords());
        return new TexturedModel(rawModel, texture);
    }

    private TexturedModel getTexturedModel(String fileName, boolean setTransparent) throws URISyntaxException, IOException {
        ModelData modelData = ObjectLoader.loadObjectModel(fileName);
        RawModel rawModel = loader.loadToVAO(modelData.getVertices(), modelData.getIndices(), modelData.getNormals(), modelData.getTextureCoords());
        ModelTexture texture =  new ModelTexture(loader.loadObjectTexture(fileName));
        texture.isTransparent(setTransparent);
        return new TexturedModel(rawModel, texture);
    }
}
