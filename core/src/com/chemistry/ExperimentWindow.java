package com.chemistry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.chemistry.dto.Equipment;
import com.chemistry.dto.InventorySlot;
import com.chemistry.dto.Substance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.chemistry.ExperimentChooseWindow.choosenExperiment;

public class ExperimentWindow implements Screen {
    final ChemistryModelingGame game;

    private final Texture experimentBackground;
    private final Texture inventoryTexture;
    private final Texture chemist;
    private final Texture dialogBg;
    private final BitmapFont expFont;
    private final BitmapFont slotTextFont;
    private final Texture choosedSlotTexture;

    private final DBHandler handler = new DBHandler();
    private final ReactionHandler reactionHandler = new ReactionHandler();

    private final OrthographicCamera camera;

    private final ArrayList<Substance> usedSubstances = new ArrayList<>();
    public static ArrayList<Equipment> usedEquipment = new ArrayList<>();
    private final ArrayList<InventorySlot> inventory = new ArrayList<>();
    public static ArrayList<String> phraseArray = new ArrayList<>();

    public static Rectangle mouseSpawnerRect;

    private final Rectangle exitBtn;

    public static Integer x_pos;
    public static Integer y_pos;
    public static Boolean startSpawn = false;
    public static Boolean deleteFromInventory = false;
    public static Integer choosedSubstance;
    public static Boolean inventorySlotIsPicked = false;

    public static String phrase = "";

    public ExperimentWindow(ChemistryModelingGame game) throws SQLException, ClassNotFoundException {
        this.game = game;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("GOST_A.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
        parameter.size = 24;
        parameter.color = Color.BLACK;
        expFont = generator.generateFont(parameter);
        generator.dispose();

        FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal("GOST_A.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter1 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter1.characters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
        parameter1.size = 28;
        parameter1.color = Color.BLACK;
        slotTextFont = generator1.generateFont(parameter1);
        generator1.dispose();

        MyInputListener inputListener = new MyInputListener();
        Gdx.input.setInputProcessor(inputListener);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        mouseSpawnerRect = new Rectangle(); //On a click we spawn a rectangle upon the coordinates.
        mouseSpawnerRect.setPosition(-100,-100); //If nearby exists another rectangle - go methods.
        mouseSpawnerRect.setSize(3, 3);

        exitBtn = new Rectangle();
        exitBtn.setPosition(38, 0);

        experimentBackground = new Texture(choosenExperiment.getTexture_path());
        inventoryTexture = new Texture("inventory.png");
        chemist = new Texture("chemist.png");
        dialogBg = new Texture("dialog.png");
        choosedSlotTexture = new Texture("choosedSlot.png");

        for (int i = 0; i<3; i++){
            InventorySlot slot = new InventorySlot();
            slot.setX(49F);
            slot.setY((float) (320 + (i * 78) - 74));
            slot.setSize(110F, 74F);
            slot.setSlotId(i);
            slot.setSlotTexture("");
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
                tempSubstance.setSmallTexturePath(substanceItself.getString(AllConstants.SubsConsts.SMALL_TEXTURE));
                tempSubstance.setFound_amount(substanceItself.getString(AllConstants.SubsConsts.FOUND_AMOUNT));
                tempSubstance.setOxid_amount(substanceItself.getString(AllConstants.SubsConsts.OXID_AMOUNT));
                tempSubstance.setSize(tempSubstance.getTexture_path().getWidth(), tempSubstance.getTexture_path().getHeight());
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
                tempEquip.setSize(tempEquip.getTexture_path().getWidth(), tempEquip.getTexture_path().getHeight());
                tempEquip.setSetOnPlace(false);
                tempEquip.setxAfter(equipItself.getString(AllConstants.EquipConsts.X_AFTER));
                tempEquip.setyAfter(equipItself.getString(AllConstants.EquipConsts.Y_AFTER));
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

        phraseProcessing();

        game.batch.begin();
        game.batch.draw(experimentBackground,0,0);
        game.batch.draw(inventoryTexture, 38, 230);
        game.batch.draw(chemist, 1280-40-241, 0);
        game.batch.draw(dialogBg, 1270-40-345, 270); // available space - 390-160(y) 920-1190(x)

        for (int i = 0; i<=phraseArray.size()-1; i++){
            expFont.draw(this.game.batch, phraseArray.get(i), 920, 720 - 160 - i * 35);
        }

        for (Substance subs : usedSubstances){
            game.batch.draw(subs.getTexture_path(), subs.getX(), 720 - subs.getY() - subs.getHeight());
        }

        for (Equipment equip: usedEquipment) {
            game.batch.draw(equip.getTexture_path(), equip.getX(), 720-equip.getY()-equip.getHeight());
        }


        for (InventorySlot slot : inventory){
            if (slot.getThisSlotPicked()){
                game.batch.draw(choosedSlotTexture, slot.getX()-3, 720-slot.getY()-slot.getHeight()-2);
            }
            slotTextFont.draw(this.game.batch,slot.getSlotTexture(), slot.getX()+5, 720-slot.getY()-25);
            //game.batch.draw(slot.getSlotTexture(), slot.getX(), 720-slot.getY()-slot.getHeight());
        }

        game.batch.end();
        if(startSpawn){  //Checking overlapsing of mouseSpawnerRect and other thingies
            for (Equipment equip: usedEquipment) {
                if (equip.overlaps(mouseSpawnerRect)){
                    if (!equip.getSetOnPlace()){ //move for a first time
                        phrase = equip.getName() + " теперь на месте";
                        equip.setPosition(Float.parseFloat(equip.getxAfter()), 720 - Float.parseFloat(equip.getyAfter()));
//                        equip.setPosition(450, 720 - 150 - equip.getHeight());
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

                            equip.addSubstance(substanceInSlotId);
                            for (Substance substance : usedSubstances){
                                if (substance.getSubId().equals(substanceInSlotId)){
                                    phrase = "Добавил " + substance.getName() + " в минзурку!";
                                }
                            }
                            // Need a normal check if substance is already added
                            if (equip.getSubstancesInside().size()>=2){
                                try {
                                    reactionHandler.getSubstancesFromEquipment(equip);
                                } catch (SQLException | ClassNotFoundException throwables) {
                                    throwables.printStackTrace();
                                }
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

                        } else phrase = "Вы ничего не выбрали...";
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
                            phrase = "Добавил в инвентарь: " + subs.getName();
                            break;
                        }
                        phrase = "Инвентарь заполнен...";
                    }
                    break;
                }
            }

            for (InventorySlot slot : inventory){
                if (slot.overlaps(mouseSpawnerRect)){
                    if (deleteFromInventory) {
                        if (slot.getSubstanceIdInSlot().isEmpty()) {
                            phrase = "Пустовато тут...";
                        } else {
                            slot.setSubstanceIdInSlot("");
                            slot.setSlotTexture("");
                            phrase = "Убрал содержимое слота номер " + slot.getSlotId()+1;
                            if (slot.getThisSlotPicked()){
                                inventorySlotIsPicked = false;
                                slot.setThisSlotPicked(false);
                                phrase += ". Этот слот был выбранным ранее! ";
                            }
                        }
                        break;
                    } else {
                        int slotTryingToPickId = slot.getSlotId();
                        if (inventorySlotIsPicked && slot.getThisSlotPicked()){
                            phrase = slot.getSlotId()+1 + " слот больше не выбран";
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
                                if (!slot.getSubstanceIdInSlot().equals("")) {
                                    for (Substance substance : usedSubstances) {
                                        if (substance.getSubId().equals(slot.getSubstanceIdInSlot())) {
                                            phrase = "Этот слот теперь выбран для работы. В нем находится - " + substance.getName();
                                        }
                                    }
                                    slot.setThisSlotPicked(true);
                                    inventorySlotIsPicked = true;
                                } else phrase = "Вы не можете выбрать пустой слот для работы!";
                            } else {
                                for (InventorySlot slot2: inventory){
                                    if (slotTryingToPickId != slot2.getSlotId()){
                                        slot2.setThisSlotPicked(false);
                                    } else slot2.setThisSlotPicked(true);
                                }
                                for (Substance substance : usedSubstances){
                                    if (substance.getSubId().equals(slot.getSubstanceIdInSlot())){
                                        phrase = "Выбранный слот изменен на другой, в нем находится - " + substance.getName();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            startSpawn = false;
        }

    }

    public void phraseProcessing(){
        ArrayList<String> newPhraseArr = new ArrayList<>();
        if (phrase.length() > 30){
            String tempString = "";
            String[] tempArray = phrase.trim().split(" ");
            for (int i = 0; i<=tempArray.length-1; i++){
                if (tempString.length() + tempArray[i].length()<31){
                    tempString += tempArray[i] + " ";
                } else {
                    newPhraseArr.add(tempString);
                    tempString = "";
                    i--;
                }
            }
            newPhraseArr.add(tempString);
        } else newPhraseArr.add(phrase);
        if (newPhraseArr.size() > 8){
            newPhraseArr = new ArrayList<>();
        }
        phraseArray = newPhraseArr;
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
