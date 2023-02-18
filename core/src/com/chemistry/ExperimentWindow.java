package com.chemistry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.chemistry.dto.Equipment;
import com.chemistry.dto.InventorySlot;
import com.chemistry.dto.Substance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.chemistry.CustomExperimentWindow.createInGameNameForSubstance;
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
    private final ReactionHandler reactionHandler = new ReactionHandler();
    public static Music soundPlaying = Gdx.audio.newMusic(Gdx.files.internal("Sounds/dispose.mp3"));;

    private final Rectangle chemistRect;

    private final OrthographicCamera camera;

    private final ArrayList<Substance> usedSubstances = new ArrayList<>();
    public static ArrayList<Equipment> usedEquipment = new ArrayList<>();
    private final ArrayList<InventorySlot> inventory = new ArrayList<>();
    public static ArrayList<String> phraseArray = new ArrayList<>();

    public static Rectangle mouseSpawnerRect;

    public static Integer x_pos;
    public static Integer y_pos;
    public static Boolean startSpawn = false;
    public static Boolean deleteFromInventory = false;
    public static Integer choosedSubstance;
    public static Boolean inventorySlotIsPicked = false;
    public static Boolean closeWindow = false;
    public static Boolean animationStarted = false;
    public static String phrase = "Для выхода на главный экран нажмите Esc";
    public static Substance animatedSubstance = new Substance();
    public static Boolean waitForAddition = false;
    public static Sprite animationTexture;
    public static Array<String> effectsQueue = new Array<>();
    public SpriteBatch animationBatch;
    private static AnimationController animationController;
    public ExperimentWindow(ChemistryModelingGame game) throws SQLException, ClassNotFoundException {
        this.game = game;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("appetite.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
        parameter.size = 18;
        parameter.color = Color.BLACK;
        expFont = generator.generateFont(parameter);
        generator.dispose();

        FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal("appetite.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter1 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter1.characters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
        parameter1.size = 20;
        parameter1.color = Color.BLACK;
        slotTextFont = generator1.generateFont(parameter1);
        generator1.dispose();

        ExperimentWindowInputListener inputListener = new ExperimentWindowInputListener();
        Gdx.input.setInputProcessor(inputListener);

        chemistRect = new Rectangle();
        chemistRect.setSize(100, 100);
        chemistRect.setPosition(1000, 450);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        mouseSpawnerRect = new Rectangle(); //On a click we spawn a rectangle upon the coordinates.
        mouseSpawnerRect.setPosition(-100,-100); //If nearby exists another rectangle - go methods.
        mouseSpawnerRect.setSize(3, 3);

        experimentBackground = new Texture("Textures/" + choosenExperiment.getTexture_path());
        inventoryTexture = new Texture("Textures/" +"inventory.png");
        chemist = new Texture("Textures/" +"chemist.png");
        dialogBg = new Texture("Textures/" +"dialog.png");
        choosedSlotTexture = new Texture("Textures/" +"choosedSlot.png");

        animationBatch = new SpriteBatch();

        for (int i = 0; i<3; i++){
            InventorySlot slot = new InventorySlot();
            slot.setX(49F);
            slot.setY((float) (320 + (i * 78) - 74));
            slot.setSize(110F, 74F);
            slot.setSlotId(i);
            slot.setSlotTexture("");
            slot.setThisSlotPicked(false);
            slot.setSubstanceInSlot(new Substance());
            inventory.add(slot);
        }

        DBHandler handler = new DBHandler();
        ResultSet substancesIDS = handler.getUsingSubstancesIDs(choosenExperiment.getExp_id()); // Setting all usable substances
        while (substancesIDS.next()){
            ResultSet substanceItself = handler.getSubstanceByID(substancesIDS.getString(AllConstants.SubsExpConsts.SUBS_EXP_ID));
            Substance tempSubstance = new Substance();
            if (substanceItself.next()){
                tempSubstance.setSubId(substanceItself.getString(AllConstants.SubsConsts.ID));
                tempSubstance.setTexture(new Texture("Textures/" + substanceItself.getString(AllConstants.SubsConsts.TEXTURE_PATH)));
                tempSubstance.setName(substanceItself.getString(AllConstants.SubsConsts.NAME));

                tempSubstance.setFoundation(substanceItself.getString(AllConstants.SubsConsts.FOUND_PART_NAME));
                tempSubstance.setOxid(substanceItself.getString(AllConstants.SubsConsts.OXID_PART_NAME));
                tempSubstance.setFound_amount(substanceItself.getString(AllConstants.SubsConsts.FOUND_AMOUNT));
                tempSubstance.setOxid_amount(substanceItself.getString(AllConstants.SubsConsts.OXID_AMOUNT));
                tempSubstance.setSubstanceType(substanceItself.getString(AllConstants.SubsConsts.SUBSTANCE_TYPE));

                tempSubstance.setSubstanceNameInGame(createInGameNameForSubstance(
                        tempSubstance.getFoundation() + "-" + tempSubstance.getFound_amount() + " " +
                                tempSubstance.getOxid() + "-" + tempSubstance.getOxid_amount()));

                tempSubstance.setSize(tempSubstance.getTexture().getWidth(), tempSubstance.getTexture().getHeight());
                tempSubstance.setUnstable_type(substanceItself.getString(AllConstants.SubsConsts.UNSTABLE_TYPE));
                ResultSet substanceExpConn = handler.getSubstanceByIDInSubsExpsTableForExpWindow(
                        substanceItself.getString(AllConstants.SubsConsts.ID), choosenExperiment.getExp_id());
                if (substanceExpConn.next()){
                    tempSubstance.setX(Float.parseFloat(substanceExpConn.getString(AllConstants.SubsExpConsts.SUBS_X)));
                    tempSubstance.setY(720 - Float.parseFloat(substanceExpConn.getString(AllConstants.SubsExpConsts.SUBS_Y)) - tempSubstance.getTexture().getHeight());
                }
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
                tempEquip.setTexture_path(new Texture("Textures/" + equipItself.getString(AllConstants.EquipConsts.TEXTURE_PATH)));
                tempEquip.setSize(tempEquip.getTexture_path().getWidth(), tempEquip.getTexture_path().getHeight());
                tempEquip.setSetOnPlace(false);
                ResultSet equipExpConn = handler.getEquipmentByIDInEquipExpTableForExpWindow(
                        equipItself.getString(AllConstants.EquipConsts.ID), choosenExperiment.getExp_id());
                if (equipExpConn.next()){
                    tempEquip.setX(Float.parseFloat(equipExpConn.getString(AllConstants.EquipExpConsts.EQUIP_X)));
                    tempEquip.setY(720 - Float.parseFloat(equipExpConn.getString(AllConstants.EquipExpConsts.EQUIP_Y)) - tempEquip.getHeight());
                }
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

        if (closeWindow){
            try {
                game.setScreen(new ExperimentChooseWindow(game));
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            phrase = "";
            startSpawn = false;
            deleteFromInventory = false;
            inventorySlotIsPicked = false;
            closeWindow = false;
            usedEquipment = new ArrayList<>();
            phraseArray = new ArrayList<>();
        }

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
            if (subs != animatedSubstance) {
                game.batch.draw(subs.getTexture(), subs.getX(), 720 - subs.getY() - subs.getHeight());
            }
        }

        for (Equipment equip: usedEquipment) {
            game.batch.draw(equip.getTexture_path(), equip.getX(), 720-equip.getY()-equip.getHeight());
        }


        for (InventorySlot slot : inventory){
            if (slot.getThisSlotPicked()){
                game.batch.draw(choosedSlotTexture, slot.getX()-3, 720-slot.getY()-slot.getHeight()-2);
            }
            slotTextFont.draw(this.game.batch,slot.getSlotTexture(), slot.getX()+5, 720-slot.getY()-25);
        }

        if (animationStarted){
            Vector2 animatedXY = animationController.Move();
            animationTexture.setPosition(animatedXY.x, animatedXY.y);
            animationTexture.draw(game.batch);
        }

        game.batch.end();
        if(startSpawn){  //Checking overlapsing of mouseSpawnerRect and other thingies
            if (mouseSpawnerRect.overlaps(chemistRect)){
                for (Substance substance : usedSubstances){
                    for (Substance substance1 : usedSubstances){
                        if (usedEquipment.get(0).getSubstancesInside().size() < 2){
                            usedEquipment.get(0).addSubstance(substance);
                            usedEquipment.get(0).addSubstance(substance1);
                        }

                        if (usedEquipment.get(0).getSubstancesInside().size() == 2){
                            try {
                                reactionHandler.getSubstancesFromEquipment(usedEquipment.get(0));
                            } catch (SQLException | ClassNotFoundException e) {
                                System.out.println("Неудачная реакция - " + substance + " + " + substance1);
                            }
                        }
                    }
                }
            }

            for (Equipment equip: usedEquipment) {
                if (equip.overlaps(mouseSpawnerRect)){
                    if (inventorySlotIsPicked && equip.getSubstancesInside().size() < 2 || waitForAddition){
                        Substance substanceInSlotId = new Substance();
                        for (InventorySlot slot: inventory) {
                            if (slot.getThisSlotPicked()){
                                if (!slot.getSubstanceInSlot().getSubstanceNameInGame().equals("Ничего")){
                                    substanceInSlotId = slot.getSubstanceInSlot();
                                    break;
                                }
                            }
                        } //extended functiosubstanceInSlotIdns lower incoming... \|/
                        equip.addSubstance(substanceInSlotId);
                        phrase = "Добавил " + substanceInSlotId.getName() + " " + equip.getName() + "!";
                        animatedSubstance = substanceInSlotId;
                        animationTexture = new Sprite(animatedSubstance.getTexture());
                        animationController = new AnimationController(new Vector2
                                (substanceInSlotId.getX(), 720-substanceInSlotId.getY()-substanceInSlotId.getHeight()),
                                new Vector2(equip.getX(), 720-equip.getY()), animationTexture);
                        animationController.CalculateSpeed();
                        animationController.CalculateDirection();

                        // Need a normal check if substance is already added
                    if (equip.getSubstancesInside().size()>=2){
                        if (waitForAddition){
                            try {
                                reactionHandler.StartOVR_Reaction(substanceInSlotId);
                            } catch (SQLException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                            waitForAddition = false;
                        } else {
                            try {
                                reactionHandler.getSubstancesFromEquipment(equip);
                            } catch (SQLException | ClassNotFoundException throwables) {
                                throwables.printStackTrace();
                            }
                        }
                    }

                    if (effectsQueue.size > 1){
                        //start effects animations
                    }

                    } else {
                        reactionHandler.clearEquipment();
                        PlaySound("dispose");
                        phrase = "Выкинул все шо было";
                    }
                }
            }

            for (Substance subs : usedSubstances){
                if (subs.overlaps(mouseSpawnerRect)){
                    choosedSubstance = Integer.valueOf(subs.getSubId());
                    for (InventorySlot slot : inventory){
                        if(slot.getSubstanceInSlot().getSubstanceNameInGame().equals("Ничего")){
                            slot.setSlotTexture(subs.getSubstanceNameInGame());
                            slot.setSubstanceInSlot(subs);
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
                        if (slot.getSubstanceInSlot().getSubstanceNameInGame().equals("Ничего")) {
                            phrase = "Пустовато тут...";
                        } else {
                            slot.setSubstanceInSlot(new Substance());
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
                                if (!slot.getSubstanceInSlot().getSubstanceNameInGame().equals("Ничего")) {
                                    for (Substance substance : usedSubstances) {
                                        if (substance.getSubId().equals(slot.getSubstanceInSlot().getSubId())) {
                                            phrase = "Этот слот теперь выбран для работы. В нем находится - " + substance.getName();
                                        }
                                    }
                                    slot.setThisSlotPicked(true);
                                    inventorySlotIsPicked = true;
                                } else phrase = "Вы не можете выбрать пустой слот для работы!";
                            } else {
                                for (InventorySlot slot2: inventory){
                                    slot2.setThisSlotPicked(slotTryingToPickId == slot2.getSlotId());
                                }
                                for (Substance substance : usedSubstances){
                                    if (substance.getSubId().equals(slot.getSubstanceInSlot().getSubId())){
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
            StringBuilder tempString = new StringBuilder();
            String[] tempArray = phrase.trim().split(" ");
            for (int i = 0; i<=tempArray.length-1; i++){
                if (tempString.length() + tempArray[i].length()<31){
                    tempString.append(tempArray[i]).append(" ");
                } else {
                    newPhraseArr.add(tempString.toString());
                    tempString = new StringBuilder();
                    i--;
                }
            }
            newPhraseArr.add(tempString.toString());
        } else newPhraseArr.add(phrase);
        if (newPhraseArr.size() > 8){
            newPhraseArr = new ArrayList<>();
        }
        phraseArray = newPhraseArr;
    }

    public static void PlaySound(String sound){
        if (soundPlaying.isPlaying()) {
            soundPlaying.stop();
        }
        soundPlaying = Gdx.audio.newMusic(Gdx.files.internal("Sounds/" + sound + ".mp3"));
        soundPlaying.play();
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

