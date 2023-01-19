package com.chemistry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;

import com.chemistry.dto.MenuSlot;
import com.chemistry.dto.Substance;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.chemistry.ExperimentChooseWindow.choosenExperiment;


public class CustomExperimentWindow implements Screen {
    final ChemistryModelingGame game;
    private final OrthographicCamera camera;

    private final BitmapFont labelFont;
    private final Texture background;
    private final Texture dialogBg;
    private final Texture menu;
    private final Texture chemist;
    private final Texture menuSlotTexture;
    private final Texture menuSlotChoosen;
    private final Stage mainStage;
    private final DBHandler handler = new DBHandler();
    private final Rectangle arrowRight;
    private final Rectangle arrowLeft;
    public static Rectangle mouseSpawnerRect;

    private final Rectangle placeSpace;

    public static Integer x_pos;
    public static Integer y_pos;
    public static Boolean startSpawn = false;

    public static Boolean closeWindow = false;

    private final Array<Substance> possibleSubstancesInMenu;

    private final Array<MenuSlot> substancesMenu;

    private final Label.LabelStyle labelStyle;

    public static Integer choosedSlotId;

    public static Boolean isSomethingPicked;
    public static Boolean unpickFromMenu;
    public static Boolean rightClick;

    private final Array<Substance> substancesPlaced;


    public CustomExperimentWindow(ChemistryModelingGame game) throws SQLException, ClassNotFoundException {
        this.game = game;

        background = new Texture("exp1_bg.jpg");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        mainStage = new Stage();
        dialogBg = new Texture("dialog.png");
        menu = new Texture("menu.png");
        chemist = new Texture("chemist.png");
        menuSlotTexture = new Texture("menuSlot.png");
        menuSlotChoosen = new Texture("menuSlotChoosed.png");

        arrowLeft = new Rectangle();
        arrowLeft.setSize(95,110);
        arrowLeft.setPosition(41, 720-7-110);

        arrowRight = new Rectangle();
        arrowRight.setSize(95, 110);
        arrowRight.setPosition(1280-145, 720-7-100);

        mouseSpawnerRect = new Rectangle(); //Look ExperimentWindow.java
        mouseSpawnerRect.setPosition(-100,-100);
        mouseSpawnerRect.setSize(3, 3);

        placeSpace = new Rectangle();
        placeSpace.setPosition(40, 121);
        placeSpace.setSize(123,123); //dodelat

        CustomExperimentInputListener inputListener = new CustomExperimentInputListener();
        Gdx.input.setInputProcessor(inputListener);

//        Array<Array<String>> substances = new Array<>();

        substancesPlaced = new Array<>();

        substancesMenu = new Array<>();

        choosedSlotId = 0;
        isSomethingPicked = false;
        unpickFromMenu = false;
        rightClick = false;

        possibleSubstancesInMenu = new Array<>();

        ArrayList<String> substances = handler.getAllSubstancesNames(); // Getting all substances' names

        Array<Array<String>> subtancesInMenu = new Array<>();

        int substancesAmount = substances.size();
        int rows = Math.round(substancesAmount / 6f);
        int ostatok = substancesAmount % 6;
        if (ostatok % 6 > 0){
            rows++;
        }

        for (int i = 0; i < rows; i++){
            Array<String> tempArr = new Array<>();
            for (int j = 0; j < 6; j++){
                if (ostatok <= j){
                    tempArr.add(substances.get(j * (i + 1)));
                } else tempArr.add("Nothing");
            }
            subtancesInMenu.add(tempArr);
        }

        for (int i = 0; i < 6; i++){
            MenuSlot substance = new MenuSlot();
            substance.setX(138 + i * 166);
            substance.setSize(166, 120);
            substance.setY(715-substance.getHeight());
            substance.setSlotId(i+1);
            substance.setSlotTexture(subtancesInMenu.get(0).get(i)); //name
            substance.setThisSlotPicked(false);
            substancesMenu.add(substance);
        }
        try {
            for (int i = 0; i < 6; i++) { //setting substances names in menu
                substancesMenu.get(i).setSlotTexture(substances.get(i));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("appetite.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
        parameter.size = 16;
        parameter.color = Color.BLACK;
        labelFont = generator.generateFont(parameter);
        generator.dispose();

        labelStyle = new Label.LabelStyle();
        labelStyle.font = labelFont;
        //can not do right now

//        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
//        selectBoxStyle.font = selectBoxFont;
//
//        Skin mySkin = new Skin(Gdx.files.internal("metal/skin/metal-ui.json"));
//
//        SelectBox<String> chooseSubstance = new SelectBox<String>(mySkin.get("default", SelectBox.SelectBoxStyle.class));
//
//        TextButton button = new TextButton("bob", mySkin, "default");
//        button.setPosition(150, 150);
//
//        mainStage.addActor(button);
//        mainStage.addActor(chooseSubstance);
//
//        TextButton.TextButtonStyle buttonStyle = mySkin.get("default", TextButton.TextButtonStyle.class);
//
//        TextButton coloredbtn = new TextButton("skinnn", buttonStyle);
//
//        mainStage.addActor(coloredbtn);
    }

    @Override
    public void render(float delta) {
        game.batch.begin();
        game.batch.draw(background, 0, 0);
        game.batch.draw(chemist, 1280 - 40 - 241, 0);
        game.batch.draw(dialogBg, 1270 - 40 - 345, 270);
        game.batch.draw(menu, 38, 0);

        for (MenuSlot slot : substancesMenu) {
            if (slot.getThisSlotPicked()) {
                game.batch.draw(menuSlotChoosen, slot.getX() + 3, 5);
            } else game.batch.draw(menuSlotTexture, slot.getX() + 3, 5);
            Label textInSlot = new Label(slot.getSlotTexture(), labelStyle);
            textInSlot.setPosition(slot.getX() + 5, 5);
            textInSlot.setSize(164, 110);
            textInSlot.setAlignment(1);
            textInSlot.draw(this.game.batch, 1f);
//            game.font.draw(this.game.batch, slot.getSlotTexture(), slot.getX() + 5, 720-slot.getY()-slot.getHeight()/2 + 5);
        }

//      mainStage.draw();
        game.batch.end();

        if (startSpawn) {

            if (arrowRight.overlaps(mouseSpawnerRect)) {
                System.out.println("Right");
            }
            if (arrowLeft.overlaps(mouseSpawnerRect)) {
                System.out.println("Left");
            }

            for (MenuSlot slot : substancesMenu) {
                if (slot.overlaps(mouseSpawnerRect)) {
                    if (!unpickFromMenu && isSomethingPicked && !slot.getThisSlotPicked()){ // if something is picked and we want to swap
                        substancesMenu.get(choosedSlotId-1).setThisSlotPicked(false);
                        choosedSlotId = slot.getSlotId();
                        slot.setThisSlotPicked(true);
                        System.out.println("Repicked slot with id: " + slot.getSlotId());
                    } else if (!isSomethingPicked && !rightClick && !slot.getThisSlotPicked()){ // if nothing is picked
                        slot.setThisSlotPicked(true);
                        choosedSlotId = slot.getSlotId();
                        System.out.println("Picked slot with id: " + slot.getSlotId());
                        isSomethingPicked = true;
                        rightClick = false;
                    } else { //we unpick if right-click
                        if (slot.getThisSlotPicked() && rightClick){
                            choosedSlotId = 0;
                            slot.setThisSlotPicked(false);
                            System.out.println("Unpicked slot with id: " + slot.getSlotId());
                            isSomethingPicked = false;
                            unpickFromMenu = false;
                        } else { //for right click on not picked and then lmbc on picked
                            unpickFromMenu = false; //flag change cause else if you
                        }
                    }
                }
                startSpawn = false;
            }

            if (isSomethingPicked){

            }
        }
    }

    @Override
    public void show() {

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
