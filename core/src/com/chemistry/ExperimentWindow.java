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

    private final DBHandler handler = new DBHandler();

    private final OrthographicCamera camera;

    private final ArrayList<Substance> usedSubstances = new ArrayList<>();
    private final ArrayList<Equipment> usedEquipment = new ArrayList<>();
    private final ArrayList<Integer> inventory = new ArrayList<>(3);

    public static Rectangle mouseSpawnerRect;

    private final Rectangle exitBtn;

    public static Integer x_pos;
    public static Integer y_pos;
    public static Boolean startSpawn = false;
    public static Integer choosedSubstance;
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

        ResultSet substancesIDS = handler.getUsingSubstancesIDs(choosenExperiment.getExp_id()); // Setting all usable substances

        while (substancesIDS.next()){
            ResultSet substanceItself = handler.getSubstanceByID(substancesIDS.getString(AllConstants.SubsExpConsts.SUBS_EXP_ID));
            Substance tempSubstance = new Substance();
            if (substanceItself.next()){
                tempSubstance.setTexture_path(new Texture(substanceItself.getString(AllConstants.SubsConsts.TEXTURE_PATH)));
                tempSubstance.setName(substanceItself.getString(AllConstants.SubsConsts.NAME));
                tempSubstance.setX(Float.parseFloat(substanceItself.getString(AllConstants.SubsConsts.TEXTURE_X)));
                tempSubstance.setY(720 - Float.parseFloat(substanceItself.getString(AllConstants.SubsConsts.TEXTURE_Y)) - 200);
                tempSubstance.setFoundation(substanceItself.getString(AllConstants.SubsConsts.FOUND_PART_NAME));
                tempSubstance.setOxid(substanceItself.getString(AllConstants.SubsConsts.OXID_PART_NAME));
                tempSubstance.setSize(200, 200);
            }
            usedSubstances.add(tempSubstance);

        }

        ResultSet equipmentIDS = handler.getUsingEquipmentIDs(choosenExperiment.getExp_id());

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
        game.batch.draw(inventoryTexture, 38, 230);
        for (Substance subs : usedSubstances){
            game.batch.draw(subs.getTexture_path(), subs.getX(), 720 - subs.getY() - subs.getHeight());
        }

        for (Equipment equip: usedEquipment) {
            game.batch.draw(equip.getTexture_path(), equip.getX(), 720-(equip.getY()+equip.getHeight()));
        }
        game.batch.end();

        if(startSpawn){  //Checking overlapsing of mouseSpawnerRect and other thingies
            int i = 0;
            for (Equipment equip: usedEquipment) {
                if (equip.overlaps(mouseSpawnerRect)){
                    System.out.println("Bim");
                    equip.setPosition(300, 720 - 150 - equip.getHeight());
                    break;
                }
            }

            for (Substance subs : usedSubstances){
                i++;
                if (subs.overlaps(mouseSpawnerRect)){
                    choosedSubstance = i;
                    if (inventory.size() < 3){

                        inventory.add(choosedSubstance);
                    } else System.out.println("We are full!");
                    startSpawn = false;
                    break;
                }
            }
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
