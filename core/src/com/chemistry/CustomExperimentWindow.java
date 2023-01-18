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

    public static Integer x_pos;
    public static Integer y_pos;
    public static Boolean startSpawn = false;

    public static Boolean closeWindow = false;

    private final Array<Substance> possibleSubstancesInMenu;

    private final Array<MenuSlot> substancesMenu;

    private final Label.LabelStyle labelStyle;

    public static Integer choosedSlotId;

    public static Boolean isSomethingPicked;


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

        CustomExperimentInputListener inputListener = new CustomExperimentInputListener();
        Gdx.input.setInputProcessor(inputListener);

//        Array<Array<String>> substances = new Array<>();

        substancesMenu = new Array<>();

        choosedSlotId = 0;
        isSomethingPicked = false;

        possibleSubstancesInMenu = new Array<>();

        ArrayList<String> substances = handler.getAllSubstancesNames(); // Getting all substances' names

        Array<Array<String>> subtancesInMenu = new Array<>();

        int substancesAmount = substances.size();
        int rows = Math.round(substancesAmount / 6f);
        if (substancesAmount % 6 > 0){
            rows++;
        }
        System.out.println(substancesAmount + "   " + rows);
//        Array<String> tempArray1 = new Array<>();
//        tempArray1.add("NaOH");
//        tempArray1.add("NaCl");
//        tempArray1.add("FeSO4");
//        tempArray1.add("2Al(SO4)3");
//        tempArray1.add("NH4(OH)");
//        tempArray1.add("NH4(OH)");
//        substances.add(tempArray1);
//
//        Array<String> tempArray2 = new Array<>();
//        tempArray2.add("NaCl");
//        tempArray2.add("NaOH");
//        tempArray2.add("FeSO4");
//        tempArray2.add("2Al(SO4)3");
//        tempArray2.add("NH4(OH)");
//        tempArray2.add("2Al(SO4)3");
//        substances.add(tempArray2);

        for (int i = 0; i < 6; i++){
            MenuSlot substance = new MenuSlot();
            substance.setX(138 + i * 166);
            substance.setSize(166, 120);
            substance.setY(715-substance.getHeight());
            substance.setSlotId(i+1);
            substance.setSlotTexture("testName"); //name
            substance.setThisSlotPicked(false);
            substancesMenu.add(substance);
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
        game.batch.draw(background, 0,0);
        game.batch.draw(chemist, 1280-40-241, 0);
        game.batch.draw(dialogBg, 1270-40-345, 270);
        game.batch.draw(menu, 38, 0);

        for (MenuSlot slot : substancesMenu){
            if (slot.getThisSlotPicked()){
                game.batch.draw(menuSlotChoosen, slot.getX()+3, 5);
            } else game.batch.draw(menuSlotTexture, slot.getX()+3, 5);
            Label textInSlot = new Label(slot.getSlotTexture(), labelStyle);
            textInSlot.setPosition(slot.getX() + 5, 5);
            textInSlot.setSize(164, 110);
            textInSlot.setAlignment(1);
            textInSlot.draw(this.game.batch, 1f);
//            game.font.draw(this.game.batch, slot.getSlotTexture(), slot.getX() + 5, 720-slot.getY()-slot.getHeight()/2 + 5);
        }

//        mainStage.draw();
        game.batch.end();

        if(startSpawn) {

            if (arrowRight.overlaps(mouseSpawnerRect)) {
                System.out.println("Right");
            }
            if (arrowLeft.overlaps(mouseSpawnerRect)) {
                System.out.println("Left");
            }

            for (MenuSlot slot : substancesMenu) {
                if (slot.overlaps(mouseSpawnerRect)) {
                    slot.setThisSlotPicked(true);
                    choosedSlotId = slot.getSlotId();
                    isSomethingPicked = true;
                    System.out.println("Pressed slot with " + slot.getSlotTexture());
                }
            }

            startSpawn = false;
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
