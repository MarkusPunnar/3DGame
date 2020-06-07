package game.object.generation;

import engine.loader.ObjectLoader;
import engine.model.TexturedModel;
import engine.model.data.ModelData;
import game.object.Entity;
import game.object.env.Light;
import game.object.env.LightEntity;
import game.object.item.Coin;
import game.object.scene.Chest;
import game.object.scene.Door;
import game.object.scene.FacingDirection;
import game.state.Game;
import game.state.HandlerState;
import org.joml.Vector3f;
import util.math.structure.Triangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DormGenerator implements Generator {

    @Override
    public List<Entity> generate() throws IOException {
        List<Entity> dormEntities = new ArrayList<>();
        TexturedModel dormModel = GeneratorUtil.getTexturedModel("dorm");
        Entity dorm = new Entity.Builder(dormModel, new Vector3f(-350, 0, -250)).build();
        dormModel.getTexture().isTransparent(true);
        ModelData dormModelData = ObjectLoader.loadObjectModel("hitbox/dorm");
        dorm.setTriangles(Game.getInstance().getLoader().createTriangles(dormModelData.getVertices(), dormModelData.getIndices()));
        GeneratorUtil.setParentObject(dorm);
        dormEntities.add(dorm);
        //add lanterns
//        dormEntities.addAll(generateLanterns());
        //add doors
        dormEntities.addAll(generateDoors());
        //add beds
        dormEntities.addAll(generateBeds());
        //add nightstands
        dormEntities.addAll(generateNightstands());
        //add chests
        dormEntities.addAll(generateChests());
        return dormEntities;
    }

    private List<Entity> generateChests() throws IOException {
        List<Entity> chests = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            chests.add(generateChest(new Vector3f(-380, 9, -12.5f - 60f * i), 0));
            chests.add(generateChest(new Vector3f(-380, 9, 32.5f - 60f * i), 180));
        }
        return chests;
    }

    private Entity generateChest(Vector3f position, float rotationY) throws IOException {
        TexturedModel openChestModel = GeneratorUtil.getTexturedModel("openchest");
        TexturedModel closedChestModel = GeneratorUtil.getTexturedModel("closedchest");
        Chest chest = new Chest.Builder(closedChestModel, position, openChestModel).rotationY(rotationY).capacity(18).build();
        chest.addItem(new Coin(10));
        HandlerState.getInstance().registerInteractableEntity(chest);
        return chest;
    }

    private List<Entity> generateNightstands() throws IOException {
        List<Entity> nightstands = new ArrayList<>();
        TexturedModel nightstandModel = GeneratorUtil.getTexturedModel("nightstand");
        for (int i = 0; i < 5; i++) {
            nightstands.add(new Entity.Builder(nightstandModel, new Vector3f(-340, 9, 10 - 60f * i)).rotationY(-90).scale(new Vector3f(20)).build());
        }
        return nightstands;
    }


    private List<Entity> generateBeds() throws IOException {
        List<Entity> beds = new ArrayList<>();
        TexturedModel bedModel = GeneratorUtil.getTexturedModel("bed");
        ModelData bedModelData = ObjectLoader.loadObjectModel("hitbox/bedbox");
        List<Triangle> hitboxTriangles = Game.getInstance().getLoader().createTriangles(bedModelData.getVertices(), bedModelData.getIndices());
        for (int i = 0; i < 5; i++) {
            beds.add(generateBed(bedModel, hitboxTriangles, new Vector3f(-350, 9, 30 - 60f * i)));
            beds.add(generateBed(bedModel, hitboxTriangles, new Vector3f(-350, 9,-10 - 60f * i)));
        }
        return beds;
    }

    private Entity generateBed(TexturedModel bedModel, List<Triangle> hitboxTriangles, Vector3f position) {
        Entity bed = new Entity.Builder(bedModel, position).build();
        bed.setTriangles(hitboxTriangles);
        GeneratorUtil.setParentObject(bed);
        return bed;
    }


    private List<Entity> generateDoors() throws IOException {
        List<Entity> doors = new ArrayList<>();
        TexturedModel doorModel = GeneratorUtil.getTexturedModel("door");
        Door door = new Door.Builder(doorModel, new Vector3f(-335f, 9, 66.7f)).scale(new Vector3f(1, 0.96f, 0.96f)).facing(FacingDirection.WEST).build();
        HandlerState.getInstance().registerInteractableEntity(door);
        doors.add(door);
        door = new Door.Builder(doorModel, new Vector3f(-335f, 9, -284.8f)).scale(new Vector3f(1, 0.96f, 0.96f)).facing(FacingDirection.WEST).build();
        HandlerState.getInstance().registerInteractableEntity(door);
        doors.add(door);
        for (int i = 0; i < 5; i++) {
            door = new Door.Builder(doorModel, new Vector3f(-394f, 9, 12.1f - 60f * i)).scale(new Vector3f(1, 0.96f, 1.1f)).facing(FacingDirection.EAST).build();
            HandlerState.getInstance().registerInteractableEntity(door);
            doors.add(door);
        }
        return doors;
    }


    private List<LightEntity> generateLanterns() throws IOException {
        List<LightEntity> lanterns = new ArrayList<>();
        List<Light> lights = Game.getInstance().getActiveLights();
        TexturedModel lanternModel = GeneratorUtil.getTexturedModel("lantern");
        Vector3f lanternAttenuation = new Vector3f(0.8f, 0.0005f, 0.0001f);
        Vector3f lanternLightColour = new Vector3f(200f / 255f, 165f / 255f, 0 / 255f);
        //Add corridor lanterns
        LightEntity lantern = new LightEntity.Builder(lanternModel, new Vector3f(-441, 27.8f, 62.8f)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .offset(new Vector3f(4, 0, 0)).regionMin(new Vector3f(-440, 9, 10))
                .regionMax(new Vector3f(-335, 40, 86)).pointLight(true).scale(new Vector3f(0.8f)).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        lantern = new LightEntity.Builder(lanternModel, new Vector3f(-441, 27.8f, -46f)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .offset(new Vector3f(7, 0, 0)).regionMin(new Vector3f(-440, 9, -100))
                .regionMax(new Vector3f(-395, 40, 0)).pointLight(true).scale(new Vector3f(0.8f)).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        lantern = new LightEntity.Builder(lanternModel, new Vector3f(-441, 27.8f, -162f)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .offset(new Vector3f(7, 0, 0)).regionMin(new Vector3f(-440, 9, -220))
                .regionMax(new Vector3f(-395, 40, -120)).pointLight(true).scale(new Vector3f(0.8f)).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        lantern = new LightEntity.Builder(lanternModel, new Vector3f(-441, 27.8f, -287f)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .offset(new Vector3f(4, 0, 0)).regionMin(new Vector3f(-440, 9, -306))
                .regionMax(new Vector3f(-335, 40, -220)).pointLight(true).scale(new Vector3f(0.8f)).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        //Add room lanterns
        lantern = new LightEntity.Builder(lanternModel, new Vector3f(-340, 22.6f, 10)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .offset(new Vector3f(-2, 5, 0)).regionMin(new Vector3f(-440, 9, -20))
                .regionMax(new Vector3f(-335, 40, 35)).pointLight(true).scale(new Vector3f(0.8f)).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        lantern = new LightEntity.Builder(lanternModel, new Vector3f(-340, 22.6f, -50)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .offset(new Vector3f(-2, 5, 0)).regionMin(new Vector3f(-440, 9, -80))
                .regionMax(new Vector3f(-335, 40, -25)).pointLight(true).scale(new Vector3f(0.8f)).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        lantern = new LightEntity.Builder(lanternModel, new Vector3f(-340, 22.6f, -110)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .offset(new Vector3f(-2, 5, 0)).regionMin(new Vector3f(-440, 9, -140))
                .regionMax(new Vector3f(-335, 40, -85)).pointLight(true).scale(new Vector3f(0.8f)).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        lantern = new LightEntity.Builder(lanternModel, new Vector3f(-340, 22.6f, -170)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .offset(new Vector3f(-2, 5, 0)).regionMin(new Vector3f(-440, 9, -200))
                .regionMax(new Vector3f(-335, 40, -145)).pointLight(true).scale(new Vector3f(0.8f)).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        lantern = new LightEntity.Builder(lanternModel, new Vector3f(-340, 22.6f, -230)).colour(lanternLightColour).attenuation(lanternAttenuation)
                .offset(new Vector3f(-2, 5, 0)).regionMin(new Vector3f(-440, 9, -260))
                .regionMax(new Vector3f(-335, 40, -205)).pointLight(true).scale(new Vector3f(0.8f)).build();
        lights.add(lantern.getLight());
        lanterns.add(lantern);
        return lanterns;
    }
}
