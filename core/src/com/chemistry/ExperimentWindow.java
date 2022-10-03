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
    private final DBHandler handler = new DBHandler();
    private final OrthographicCamera camera;
    private final ArrayList<Substance> usedSubstances = new ArrayList<>();
    public static Rectangle mouseSpawnerRect;
    public static Integer x_pos;
    public static Integer y_pos;
    public static Boolean startSpawn = false;
    public ExperimentWindow(ChemistryModelingGame game) throws SQLException, ClassNotFoundException {
        this.game = game;

        MyInputListener inputListener = new MyInputListener();
        Gdx.input.setInputProcessor(inputListener);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        mouseSpawnerRect = new Rectangle(); //On a click we spawn a rectangle upon the coordinates.
        mouseSpawnerRect.setPosition(-100,-100); //If nearby exists another rectangle - go methods.
        mouseSpawnerRect.setSize(10, 10);

        experimentBackground = new Texture(choosenExperiment.getTexture_path());

        ResultSet substancesIDS = handler.getUsingSubstancesIDs(choosenExperiment.getExp_id());

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
                System.out.println(tempSubstance.width);
            }
            usedSubstances.add(tempSubstance);

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
        for (Substance subs : usedSubstances){
            game.batch.draw(subs.getTexture_path(), subs.getX(), 720 - subs.getY() - subs.getHeight());
        }
        game.batch.end();

        if(startSpawn){
            for (Substance subs : usedSubstances){
                if (subs.overlaps(mouseSpawnerRect)){
                    System.out.println("Working?");
                    startSpawn = false;
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
