package com.chemistry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import com.chemistry.dto.Equipment;
import com.chemistry.dto.Experiment;
import com.chemistry.dto.MenuSlot;
import com.chemistry.dto.Substance;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
public class CustomExperimentWindow implements Screen {
    final ChemistryModelingGame game;
    private final Texture background;
    private final Texture dialogBg;
    private final Texture menu;
    private final Texture chemist;
    private final Texture menuSlotTexture;
    private final Texture menuSlotChoosen;
    private final Texture equipMenu;
    private final Texture equipMenuSlot;
    private final Texture equipMenuSlotChoosed;
    private static final DBHandler handler = new DBHandler();
    private final Rectangle arrowRight;
    private final Rectangle arrowLeft;
    private final Rectangle arrowSubstanceMenuRight;
    private final Rectangle arrowSubstanceMenuLeft;
    private final MenuSlot equipSlot;
    public static Rectangle mouseSpawnerRect;
    private final Rectangle placeSpace;
    public static Integer x_pos;
    public static Integer y_pos;
    public static Boolean startSpawn = false;
    public static Boolean closeWindow = false;
    private Array<MenuSlot> substancesMenu;
    private final Label.LabelStyle labelStyle;
    public static Integer choosedSlotId;
    public static Boolean isSomethingPicked;
    public static Boolean unpickFromMenu;
    public static Boolean rightClick;
    private Boolean equipmentPicked;
    private Boolean saveMenuEnabled;
    private final Array<Substance> substancesPlaced;

    private final Array<Equipment> equipmentPlaced;
    private final ArrayList<String> equipments;
    private final Array<Array<Substance>> subtancesInMenu;
    private int equipmentPlacedElementId;
    private final Label saveButtonLabel;
    private final Rectangle saveRect;
    private Substance substancePicked;
    private Equipment equipPickedFromMenu;
    private final Label equipMenuLabel;
    private final Stage saveMenuStage;
    private final TextField saveMenuExpNameTF;
    private int substanceMenuRowPicked = 0;

    //OPTIMIZATION (YESSSSSSSSSSS)
    private final Rectangle customScreenRect;
    //end of it
    public CustomExperimentWindow(final ChemistryModelingGame game) throws SQLException, ClassNotFoundException {
        this.game = game;

        background = new Texture("exp1_bg.jpg");
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        dialogBg = new Texture("dialog.png");
        menu = new Texture("menu.png");
        chemist = new Texture("chemist.png");
        menuSlotTexture = new Texture("menuSlot.png");
        menuSlotChoosen = new Texture("menuSlotChoosed.png");
        equipMenu = new Texture("equipMenu.png");
        equipMenuSlot = new Texture("equipMenuSlot.png");
        equipMenuSlotChoosed = new Texture("equipMenuSlotChoosed.png");
        Texture saveMenuTexture = new Texture("saveMenu.png");

        //optimization
        customScreenRect = new Rectangle();
        customScreenRect.setSize(1280, 720);
        customScreenRect.setPosition(0,0);
        //end

        arrowLeft = new Rectangle();
        arrowLeft.setSize(95,110);
        arrowLeft.setPosition(41, 720-7-110);

        arrowRight = new Rectangle();
        arrowRight.setSize(95, 110);
        arrowRight.setPosition(1280-145, 720-7-100);

        arrowSubstanceMenuRight = new Rectangle();
        arrowSubstanceMenuRight.setSize(95, 110);
        arrowSubstanceMenuRight.setPosition(1280-135,1);

        arrowSubstanceMenuLeft = new Rectangle();
        arrowSubstanceMenuLeft.setSize(95, 110);
        arrowSubstanceMenuLeft.setPosition(905,1);

        mouseSpawnerRect = new Rectangle(); //Look ExperimentWindow.java
        mouseSpawnerRect.setPosition(-100,-100);
        mouseSpawnerRect.setSize(3, 3);

        equipSlot = new MenuSlot();
        equipSlot.setSize(140, 110);
        equipSlot.setPosition(1000, 1);
        equipSlot.setThisSlotPicked(false);
        equipSlot.setSlotId(0);

        placeSpace = new Rectangle();
        placeSpace.setPosition(40, 0);
        placeSpace.setSize(850,550);

        final CustomExperimentInputListener inputListener = new CustomExperimentInputListener();
        Gdx.input.setInputProcessor(inputListener);

        substancesPlaced = new Array<>();

        substancesMenu = new Array<>();

        choosedSlotId = 0;

        isSomethingPicked = false;
        unpickFromMenu = false;
        rightClick = false;
        equipmentPicked = false;
        saveMenuEnabled = false;

        substancePicked = new Substance();
        equipPickedFromMenu = new Equipment();

        saveMenuStage = new Stage();
        Image stageMenuImage = new Image(saveMenuTexture);
        stageMenuImage.setPosition(1280/2f- saveMenuTexture.getWidth()/2f, 720/2f- saveMenuTexture.getHeight()/2f);
        saveMenuStage.addActor(stageMenuImage);

        ArrayList<Substance> substances = handler.getAllSubstances(); // Getting all substances' names

        subtancesInMenu = new Array<>();

        equipmentPlaced = new Array<>();

        int substancesAmount = substances.size();
        int rows = Math.round(substancesAmount / 6f);
        int ostatok = substancesAmount % 6;
        if (ostatok % 6 > 0 && rows * 6 < substancesAmount){
            rows++;
        }

        equipments = handler.getAllEquipmentsNames();
        equipmentPlacedElementId = 0;
        if (equipments.size()>0){
            equipSlot.setSlotSubstanceName(equipments.get(0));
        } else equipSlot.setSlotSubstanceName("Ничего нет");

        for (int i = 0; i < rows; i++){
            Array<Substance> tempArr = new Array<>();
            for (int j = 0; j < 6; j++){
                if (i == rows-1) {
                    if (i*6+j< substances.size()) {
                        if (ostatok-1 >= j) {
                            tempArr.add(substances.get(j + i * 6));
                        } else tempArr.add(new Substance());
                    } else {
                        tempArr.add(new Substance());
                    }
                } else tempArr.add(substances.get(j + i*6));
            }
            subtancesInMenu.add(tempArr);
        }

        resetSubstancesInMenu();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("appetite.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
        parameter.size = 16;
        parameter.color = Color.BLACK;
        BitmapFont labelFont = generator.generateFont(parameter);
        generator.dispose();

        labelStyle = new Label.LabelStyle();
        labelStyle.font = labelFont;

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = labelFont;
        textFieldStyle.fontColor = new Color(0.7f, 0.7f, 0.7f, 1);

        saveMenuExpNameTF = new TextField("", textFieldStyle);
        saveMenuExpNameTF.setPosition(470, 400);
        saveMenuExpNameTF.setMessageText("Введите название для эксперимента:");
        saveMenuExpNameTF.setWidth(370);
        saveMenuStage.addActor(saveMenuExpNameTF);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = labelFont;

        saveButtonLabel = new Label("Сохранить?", labelStyle);
        saveButtonLabel.setSize(100,30);
        saveButtonLabel.setAlignment(1);
        saveButtonLabel.setPosition(1050, 200);

        final Button dismissBtn = new TextButton("Отменить?", buttonStyle);
        saveMenuStage.addActor(dismissBtn);
        dismissBtn.setPosition(470, 300);

        dismissBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                saveMenuEnabled = false;
                Gdx.input.setInputProcessor(inputListener);
                saveMenuExpNameTF.setText("");
            }
        });

        final Button acceptBtn = new TextButton("Подтвердить!", buttonStyle);
        saveMenuStage.addActor(acceptBtn);
        acceptBtn.setPosition(680, 300);
        acceptBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (substancesPlaced.size!=0 && equipmentPlaced.size!=0) {
                    Experiment thisExperiment = new Experiment();
                    thisExperiment.setName(saveMenuExpNameTF.getText());
                    thisExperiment.setTexture_path("exp1_bg.jpg");
                    try {
                        String expId = handler.saveNewExperiment(thisExperiment);
                        handler.saveSubstances(substancesPlaced, expId);
                        handler.saveEquipment(equipmentPlaced, expId);
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    game.setScreen(new ChemistryModelingMainWindow(game));
                } else {
                    System.out.println("Nothing placed at all");
                }

            }
        });

        final Label messageLabel = new Label("", labelStyle);
        messageLabel.setColor(new Color(1f, 0f, 0f, 1));
        messageLabel.setPosition(100, 500);
        messageLabel.setSize(100, 150);
        messageLabel.setAlignment(1);
        saveMenuStage.addActor(messageLabel);

        equipMenuLabel = new Label("", labelStyle);
        equipMenuLabel.setSize(140,110);
        equipMenuLabel.setAlignment(1);
        equipMenuLabel.setPosition(1005, 720-equipSlot.getY()-equipSlot.getHeight());
        equipMenuLabel.setWrap(true);

        saveRect = new Rectangle();
        saveRect.setSize(saveButtonLabel.getWidth(), saveButtonLabel.getHeight());
        saveRect.setPosition(saveButtonLabel.getX(), 720-saveButtonLabel.getY()-saveButtonLabel.getHeight());
    }

    private void resetSubstancesInMenu() {
        substancesMenu = new Array<>();

        for (int i = 0; i < 6; i++){
            MenuSlot substance = new MenuSlot();
            substance.setX(138 + i * 166);
            substance.setSize(166, 120);
            substance.setY(715-substance.getHeight());
            substance.setSlotId(i+1);
            substance.setSlotSubstanceName(subtancesInMenu.get(substanceMenuRowPicked).get(i).getSubstanceNameInGame()); //name
            substance.setSubstanceIdInSlot(subtancesInMenu.get(substanceMenuRowPicked).get(i).getSubId()); //id
            substance.setThisSlotPicked(false);
            substancesMenu.add(substance);
        }
    }

    @Override
    public void render(float delta) {
        if (closeWindow){
            try {
                game.setScreen(new ExperimentChooseWindow(game));
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            closeWindow = false;
            this.dispose();
        }

        game.batch.begin();
        game.batch.draw(background, 0, 0);
        game.batch.draw(chemist, 1280 - 40 - 241, 0);
        game.batch.draw(dialogBg, 1270 - 40 - 345, 270);
        game.batch.draw(menu, 38, 0);
        game.batch.draw(equipMenu, 905, 720-equipMenu.getHeight());
        saveButtonLabel.draw(this.game.batch, 1f);
        for (MenuSlot slot : substancesMenu) {
            if (slot.getThisSlotPicked()) {
                game.batch.draw(menuSlotChoosen, slot.getX() + 3, 5);
            } else game.batch.draw(menuSlotTexture, slot.getX() + 3, 5);
            Label textInSlot = new Label(slot.getSlotSubstanceName(), labelStyle);
            textInSlot.setPosition(slot.getX() + 5, 5);
            textInSlot.setSize(164, 110);
            textInSlot.setAlignment(1);
            textInSlot.setWrap(true);
            textInSlot.draw(this.game.batch, 1f);
        }

        for (Substance substance : substancesPlaced) {
            game.batch.draw(substance.getTexture(), substance.getX(),
                    720 - substance.getY() - substance.getHeight());
        }

        for (Equipment equipment : equipmentPlaced) {
            game.batch.draw(equipment.getTexture_path(), equipment.getX(),
                    720 - equipment.getY() - equipment.getHeight());
        }

        if (equipSlot.getThisSlotPicked()){
            game.batch.draw(equipMenuSlotChoosed, equipSlot.getX()+6, 720-equipMenu.getHeight()+5);
        } else game.batch.draw(equipMenuSlot, equipSlot.getX()+6, 720-equipMenu.getHeight()+5);

        equipMenuLabel.setText(equipSlot.getSlotSubstanceName());
        equipMenuLabel.draw(this.game.batch, 1f);

        if (saveMenuEnabled){
            saveMenuStage.draw();
        }

        game.batch.end();

        if (startSpawn) {
            if (mouseSpawnerRect.overlaps(customScreenRect)){
                if (!rightClick) {
                    if (saveRect.overlaps(mouseSpawnerRect)){
                        Gdx.input.setInputProcessor(saveMenuStage);
                        saveMenuEnabled = true;
                    }

                    if (arrowSubstanceMenuRight.overlaps(mouseSpawnerRect)){
                        if (equipmentPlacedElementId + 1 < equipments.size()){
                            equipmentPlacedElementId++;
                            equipSlot.setSlotSubstanceName(equipments.get(equipmentPlacedElementId));
                        } else System.out.println("This is last element");
                    }

                    if (arrowSubstanceMenuLeft.overlaps(mouseSpawnerRect)){
                        if (equipmentPlacedElementId - 1 >= 0 ){
                            equipmentPlacedElementId--;
                            equipSlot.setSlotSubstanceName(equipments.get(equipmentPlacedElementId));
                        } else System.out.println("This is first element");
                    }

                    if (equipSlot.overlaps(mouseSpawnerRect) && !equipmentPicked){
                        equipSlot.setThisSlotPicked(true);
                        equipmentPicked = true;
                        mouseSpawnerRect.setPosition(-100, -100);
                        try {
                            setChoosedEquipment(equipSlot.getSlotSubstanceName());
                        } catch (SQLException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    for (Substance substance : substancesPlaced) {
                        if (substance.overlaps(mouseSpawnerRect)) {
                            mouseSpawnerRect.setPosition(-100, -100);
                        }
                    }

                    if (arrowRight.overlaps(mouseSpawnerRect)) {
                        if (substanceMenuRowPicked+1!=subtancesInMenu.size){
                            substanceMenuRowPicked++;
                            resetSubstancesInMenu();
                        } else System.out.println("This is the last row of substances");
                    }
                    if (arrowLeft.overlaps(mouseSpawnerRect)) {
                        if (substanceMenuRowPicked!=0){
                            substanceMenuRowPicked--;
                            resetSubstancesInMenu();
                        } else System.out.println("This is the first row of substances");
                    }

                    if (!equipmentPicked){
                        for (MenuSlot slot : substancesMenu) {
                            if (slot.overlaps(mouseSpawnerRect)) {
                                if (!slot.getSlotSubstanceName().equals("Ничего")) {
                                    if (!unpickFromMenu && isSomethingPicked && !slot.getThisSlotPicked()) { // if something is picked and we want to swap
                                        substancesMenu.get(choosedSlotId - 1).setThisSlotPicked(false);
                                        choosedSlotId = slot.getSlotId();
                                        slot.setThisSlotPicked(true);
                                        System.out.println("Repicked slot with id: " + slot.getSlotId());
                                        mouseSpawnerRect.setPosition(-100, -100);
                                        try {
                                            setChoosedSubstance(slot.getSubstanceIdInSlot());
                                        } catch (SQLException | ClassNotFoundException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else if (!isSomethingPicked && !rightClick && !slot.getThisSlotPicked()) { // if nothing is picked
                                        slot.setThisSlotPicked(true);
                                        choosedSlotId = slot.getSlotId();
                                        System.out.println("Picked slot with id: " + slot.getSlotId());
                                        isSomethingPicked = true;
                                        rightClick = false;
                                        try {
                                            setChoosedSubstance(slot.getSubstanceIdInSlot());
                                        } catch (SQLException | ClassNotFoundException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else { //we unpick if right-click
                                        if (slot.getThisSlotPicked()) {
                                            choosedSlotId = 0;
                                            slot.setThisSlotPicked(false);
                                            System.out.println("Unpicked slot with id: " + slot.getSlotId());
                                            isSomethingPicked = false;
                                            unpickFromMenu = false;
                                            substancePicked = new Substance();
                                        } else { //for right click on not picked and then lmbc on picked
                                            unpickFromMenu = false; //flag change cause else if you
                                        }
                                    }
                                } else System.out.println("You can't choose nothing");

                            }
                        }
                        if (isSomethingPicked && !rightClick) {
                            if (placeSpace.overlaps(mouseSpawnerRect)) {
                                setSubstanceOnTheSpace();
                            } else System.out.println("You can't place an object here!");
                        }
                    } else {
                        if (equipSlot.overlaps(mouseSpawnerRect)){
                            equipSlot.setThisSlotPicked(false);
                            equipmentPicked=false;
                        }
                        if (mouseSpawnerRect.overlaps(placeSpace)){
                            if (equipmentPlaced.size > 0) {
                                for (Equipment equipment : equipmentPlaced) {
                                    if (equipment.overlaps(mouseSpawnerRect)) {
                                        System.out.println("You can't place an object here. It's too narrow!");
                                    } else {
                                        setEquipmentOnTheSpace();
                                        break;
                                    }
                                }
                            } else {
                                setEquipmentOnTheSpace();
                            }
                        }
                    }

                } else {
                    if (placeSpace.overlaps(mouseSpawnerRect)) {
                        if (substancesPlaced.size > 0) {
                            try {
                                for (int i = 0; i < substancesPlaced.size; i++) {
                                    if (substancesPlaced.get(i).overlaps(mouseSpawnerRect)) {
                                        System.out.println("Deleted substance with name " + substancesPlaced.get(i).getName());
                                        substancesPlaced.removeIndex(i);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("Some troubles with deleting");
                            }
                        }
                        if (equipmentPlaced.size > 0) {
                            try {
                                for (int i = 0; i < equipmentPlaced.size; i++) {
                                    if (equipmentPlaced.get(i).overlaps(mouseSpawnerRect)) {
                                        System.out.println("Deleted equipment with name " + equipmentPlaced.get(i).getName());
                                        equipmentPlaced.removeIndex(i);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("Some troubles with deleting");
                            }
                        }
                    }
                }
                startSpawn = false;
            }

        }
    }
    public void setSubstanceOnTheSpace() {
        float substancePlaceX = mouseSpawnerRect.getX();
        float substancePlaceY = mouseSpawnerRect.getY();
        substancePicked.setX(substancePlaceX - substancePicked.getWidth() / 2 - 3);
        substancePicked.setY(substancePlaceY - substancePicked.getHeight() / 2);
        substancesPlaced.add(substancePicked);
        isSomethingPicked = false;
        substancesMenu.get(choosedSlotId - 1).setThisSlotPicked(false);
        substancePicked = new Substance();
    }
    public void setEquipmentOnTheSpace(){
        float equipPlaceX = mouseSpawnerRect.getX();
        float equipPlaceY = mouseSpawnerRect.getY();
        equipPickedFromMenu.setX(equipPlaceX - equipPickedFromMenu.getWidth() / 2 - 3);
        equipPickedFromMenu.setY(equipPlaceY - equipPickedFromMenu.getHeight() / 2);
        equipmentPlaced.add(equipPickedFromMenu);
        isSomethingPicked = false;
        equipmentPicked = false;
        equipSlot.setThisSlotPicked(false);
        equipPickedFromMenu = new Equipment();
    }
    public void setChoosedSubstance(String substanceId) throws SQLException, ClassNotFoundException {
        ResultSet foundSubstance = handler.findSubstanceById(substanceId); // Finding substance with name (small_texture)
        if (foundSubstance.next()) {
            substancePicked.setSubId(foundSubstance.getString(AllConstants.SubsConsts.ID));
            substancePicked.setTexture(new Texture(foundSubstance.getString(AllConstants.SubsConsts.TEXTURE_PATH)));
            substancePicked.setName(foundSubstance.getString(AllConstants.SubsConsts.NAME));
            substancePicked.setFoundation(foundSubstance.getString(AllConstants.SubsConsts.FOUND_PART_NAME));
            substancePicked.setOxid(foundSubstance.getString(AllConstants.SubsConsts.OXID_PART_NAME));
            substancePicked.setFound_amount(foundSubstance.getString(AllConstants.SubsConsts.FOUND_AMOUNT));
            substancePicked.setOxid_amount(foundSubstance.getString(AllConstants.SubsConsts.OXID_AMOUNT));
            substancePicked.setSize(substancePicked.getTexture().getWidth(), substancePicked.getTexture().getHeight());

            ResultSet substanceExpConn = handler.getSubstanceByIDInSubsExpsTable(foundSubstance.getString(AllConstants.SubsConsts.ID));
            if (substanceExpConn.next()){
                substancePicked.setX(Float.parseFloat(substanceExpConn.getString(AllConstants.SubsExpConsts.SUBS_X)));
                substancePicked.setY(720 - Float.parseFloat(substanceExpConn.getString(AllConstants.SubsExpConsts.SUBS_Y)) - 200);
            }

            System.out.println("Resetted substance with name " + substancePicked.getName());
        }
    }

    public static String createInGameNameForSubstance(String string) throws SQLException, ClassNotFoundException {
        String name = "";
        Array<String> tempArr = new Array<>(string.split(" "));

        if (!tempArr.get(0).split("-")[0].equals("0")) {
            if (Integer.parseInt(tempArr.get(0).split("-")[1]) > 1) {

                //if not simple
                if (!handler.getIsFoundationSimple(tempArr.get(0).split("-")[0])){
                    name += "(" + tempArr.get(0).split("-")[0] + ")" + tempArr.get(0).split("-")[1];
                } else {
                    name += tempArr.get(0).split("-")[0] + tempArr.get(0).split("-")[1];
                }

            }  else name += tempArr.get(0).split("-")[0];
        }

        if (!tempArr.get(1).split("-")[0].equals("0")) {
            if (Integer.parseInt(tempArr.get(1).split("-")[1]) > 1) {

                //if not simple
                if (!handler.getIsOxidizerSimple(tempArr.get(1).split("-")[0])) {
                    name += "(" + tempArr.get(1).split("-")[0] + ")" + tempArr.get(1).split("-")[1];
                } else {
                    name += tempArr.get(1).split("-")[0] + tempArr.get(1).split("-")[1];
                }


            } else name += tempArr.get(1).split("-")[0];
        }

        if (name.equals("HOH")){
            name = "H2O";
        }

        return name;
    }
    public void setChoosedEquipment(String equipName) throws SQLException, ClassNotFoundException {
        ResultSet foundEquip = handler.findEquipmentByItsName(equipName); // Finding substance with name (small_texture)
        if (foundEquip.next()) {
            equipPickedFromMenu.setId(foundEquip.getString(AllConstants.EquipConsts.ID));
            equipPickedFromMenu.setTexture_path(new Texture(foundEquip.getString(AllConstants.EquipConsts.TEXTURE_PATH)));
            equipPickedFromMenu.setName(foundEquip.getString(AllConstants.EquipConsts.NAME));
            equipPickedFromMenu.setSize(equipPickedFromMenu.getTexture_path().getWidth(), equipPickedFromMenu.getTexture_path().getHeight());
            ResultSet equipExpConn = handler.getEquipmentByIDInEquipExpTable(foundEquip.getString(AllConstants.EquipConsts.ID));
            if (equipExpConn.next()){
                equipPickedFromMenu.setX(Float.parseFloat(equipExpConn.getString(AllConstants.EquipExpConsts.EQUIP_X)));
                equipPickedFromMenu.setY(720 - Float.parseFloat(equipExpConn.getString(AllConstants.EquipExpConsts.EQUIP_Y))
                        - equipPickedFromMenu.getHeight());
            }
            System.out.println("Equipment " + equipPickedFromMenu.getName());
        }
    }
    @Override
    public void show() {}
    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
}