package com.chemistry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.chemistry.ExperimentChooseWindow.choosenExperiment;

public class ExperimentWindow implements Screen {
    final ChemistryModelingGame game;

    private final Texture experimentBackground;
    private final Texture inventoryTexture;
    private final Texture slotBasicTexture;

    private final DBHandler handler = new DBHandler();
    private final ReactionHandler reactionHandler = new ReactionHandler();

    private final OrthographicCamera camera;

    private final ArrayList<Substance> usedSubstances = new ArrayList<>();
    private final ArrayList<Equipment> usedEquipment = new ArrayList<>();
    private final ArrayList<InventorySlot> inventory = new ArrayList<>();

    public static Rectangle mouseSpawnerRect;

    private final Rectangle exitBtn;

    public static Integer x_pos;
    public static Integer y_pos;
    public static Boolean startSpawn = false;
    public static Boolean deleteFromInventory = false;
    public static Integer choosedSubstance;
    public static Boolean inventorySlotIsPicked = false;

    public ExperimentWindow(ChemistryModelingGame game) throws SQLException, ClassNotFoundException {
        this.game = game;

        MyInputListener inputListener = new MyInputListener();
        Gdx.input.setInputProcessor(inputListener);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        mouseSpawnerRect = new Rectangle(); //On a click we spawn a rectangle upon the coordinates.
        mouseSpawnerRect.setPosition(-100,-100); //If nearby exists another rectangle - go methods.
        mouseSpawnerRect.setSize(10, 10);

        exitBtn = new Rectangle();
        exitBtn.setPosition(38, 0);

        experimentBackground = new Texture(choosenExperiment.getTexture_path());
        inventoryTexture = new Texture("inventory.png");

        slotBasicTexture = new Texture("inventoryslot.png");

        for (int i = 0; i<3; i++){
            InventorySlot slot = new InventorySlot();
            slot.setX(49F);
            slot.setY((float) (320 + (i * 78) - 74));
            slot.setSize(110F, 74F);
            slot.setSlotId(i);
            slot.setSlotTexture(slotBasicTexture);
            slot.setThisSlotPicked(false);
            inventory.add(slot);
        }

        ResultSet substancesIDS = handler.getUsingSubstancesIDs(choosenExperiment.getExp_id()); // Setting all usable substances

        while (substancesIDS.next()){
            ResultSet substanceItself = handler.getSubstanceByID(substancesIDS.getString(AllConstants.SubsExpConsts.SUBS_EXP_ID));
            Substance tempSubstance = new Substance();
            if (substanceItself.next()){
                tempSubstance.setSubId(substanceItself.getString(AllConstants.SubsConsts.ID));
                tempSubstance.setTexture_path(new Texture(substanceItself.getString(AllConstants.SubsConsts.TEXTURE_PATH)));
                tempSubstance.setName(substanceItself.getString(AllConstants.SubsConsts.NAME));
                tempSubstance.setX(Float.parseFloat(substanceItself.getString(AllConstants.SubsConsts.TEXTURE_X)));
                tempSubstance.setY(720 - Float.parseFloat(substanceItself.getString(AllConstants.SubsConsts.TEXTURE_Y)) - 200);
                tempSubstance.setFoundation(substanceItself.getString(AllConstants.SubsConsts.FOUND_PART_NAME));
                tempSubstance.setOxid(substanceItself.getString(AllConstants.SubsConsts.OXID_PART_NAME));
                tempSubstance.setSmallTexturePath(new Texture(substanceItself.getString(AllConstants.SubsConsts.SMALL_TEXTURE)));
                tempSubstance.setFound_amount(substanceItself.getString(AllConstants.SubsConsts.FOUND_AMOUNT));
                tempSubstance.setOxid_amount(substanceItself.getString(AllConstants.SubsConsts.OXID_AMOUNT));
                tempSubstance.setSize(200, 200);
            }
            usedSubstances.add(tempSubstance);
        }

        ResultSet equipmentIDS = handler.getUsingEquipmentIDs(choosenExperiment.getExp_id()); // Setting equipment

        while (equipmentIDS.next()){
            ResultSet equipItself = handler.getEquipmentByID(equipmentIDS.getString(AllConstants.EquipExpConsts.EQUIP_EXP_ID));
            Equipment tempEquip = new Equipment();
            if (equipItself.next()){
                tempEquip.setId(equipItself.getString(AllConstants.EquipConsts.ID));
                tempEquip.setName(equipItself.getString(AllConstants.EquipConsts.NAME));
                tempEquip.setX(Float.parseFloat(equipItself.getString(AllConstants.EquipConsts.X_POS)));
                tempEquip.setY(Float.parseFloat(equipItself.getString(AllConstants.EquipConsts.Y_POS))-78);
                tempEquip.setTexture_path(new Texture(equipItself.getString(AllConstants.EquipConsts.TEXTURE_PATH)));
                tempEquip.setSize(55, 78);
                tempEquip.setSetOnPlace(false);
            }

            usedEquipment.add(tempEquip);
        }

    }

        @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        game.batch.setProjectionMatrix(camera.combined);
        camera.update();

        game.batch.begin();
        game.batch.draw(experimentBackground,0,0);
        game.batch.draw(inventoryTexture, 38, 230); //
        for (Substance subs : usedSubstances){
            game.batch.draw(subs.getTexture_path(), subs.getX(), 720 - subs.getY() - subs.getHeight());
        }

        for (Equipment equip: usedEquipment) {
            game.batch.draw(equip.getTexture_path(), equip.getX(), 720-equip.getY()-equip.getHeight());
        }


        for (InventorySlot slot : inventory){
            game.batch.draw(slot.getSlotTexture(), slot.getX(), 720-slot.getY()-slot.getHeight());
        }

        game.batch.end();
        if(startSpawn){  //Checking overlapsing of mouseSpawnerRect and other thingies
            for (Equipment equip: usedEquipment) {
                if (equip.overlaps(mouseSpawnerRect)){
                    if (!equip.getSetOnPlace()){ //move for a first time
                        System.out.println("Bim");
                        equip.setPosition(450, 720 - 150 - equip.getHeight());
                        equip.setSetOnPlace(true);
                        break;
                    } else {
                        if (inventorySlotIsPicked){
                            String substanceInSlotId = "";
                            for (InventorySlot slot: inventory) {
                                if (slot.getThisSlotPicked()){
                                    if (!slot.getSubstanceIdInSlot().isEmpty()){
                                        substanceInSlotId = slot.getSubstanceIdInSlot();
                                        break;
                                    }
                                }
                            } //extended functions lower incoming... \|/
                            System.out.println("We chose something in inventory and then clicked on minzurka! It was: ID " +  substanceInSlotId);

                            equip.addSubstance(substanceInSlotId);
                            System.out.println(equip.getSubstancesInside().size());
// Need a normal check if substance is already added
                            if (equip.getSubstancesInside().size()>=2){
                                try {
                                    reactionHandler.getSubstancesFromEquipment(equip);
                                } catch (SQLException | ClassNotFoundException throwables) {
                                    throwables.printStackTrace();
                                }
                                //Reaction handler
                            }

//                            if (!equip.getSubstancesInside().isEmpty()){
//                                for (String string : equip.getSubstancesInside()){
//                                    if (string.equals(substanceInSlotId)){
//                                        System.out.println("We already have this inside!");
//                                    } else {
//                                        equip.addSubstance(substanceInSlotId);
//                                        System.out.println("We added it in array...");
//                                    }
//                                }
//                            } else {
//                                System.out.println("We added it in array... cause it was empty...");
//                                equip.addSubstance(substanceInSlotId);

                        } else System.out.println("We haven't chose anything");
                    }
                }
            }

            for (Substance subs : usedSubstances){
                if (subs.overlaps(mouseSpawnerRect)){
                    choosedSubstance = Integer.valueOf(subs.getSubId());
                    for (InventorySlot slot : inventory){
                        if(slot.getSubstanceIdInSlot().isEmpty()){
                            slot.setSubstanceIdInSlot(String.valueOf(choosedSubstance));
                            slot.setSlotTexture(subs.getSmallTexturePath());
                            System.out.println(choosedSubstance);
                            break;
                        }
                    }
                    break;
                }
            }

            for (InventorySlot slot : inventory){
                if (slot.overlaps(mouseSpawnerRect)){
                    if (deleteFromInventory) {
                        if (slot.getSubstanceIdInSlot().isEmpty()) {
                            System.out.println("Empty");
                        } else {
                            System.out.println(slot.getSubstanceIdInSlot() + " deleted from " + slot.getSlotId());
                            slot.setSubstanceIdInSlot("");
                            slot.setSlotTexture(slotBasicTexture);
                            if (slot.getThisSlotPicked()){
                                inventorySlotIsPicked = false;
                                slot.setThisSlotPicked(false);
                                System.out.println("We deleted selected slot!");
                            }
                        }
                        break;
                    } else {
                        int slotTryingToPickId = slot.getSlotId();
                        if (inventorySlotIsPicked && slot.getThisSlotPicked()){
                            System.out.println("We unpicked something from inventory");
                            inventorySlotIsPicked = false;
                            slot.setThisSlotPicked(false);
                        } else {
                            int count = 0;
                            for (InventorySlot slot1: inventory) {
                                if (slot1.getThisSlotPicked()){
                                    count++;
                                }
                            }
                            if (count == 0){
                                System.out.println("We chose smth in inventory");
                                slot.setThisSlotPicked(true);
                                inventorySlotIsPicked = true;
                            } else {
                                for (InventorySlot slot2: inventory){
                                    if (slotTryingToPickId != slot2.getSlotId()){
                                        slot2.setThisSlotPicked(false);
                                    } else slot2.setThisSlotPicked(true);
                                }
                                System.out.println("So we moved chosen to another one");
                            }
                        }
                    }
                }
            }
            startSpawn = false;
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
