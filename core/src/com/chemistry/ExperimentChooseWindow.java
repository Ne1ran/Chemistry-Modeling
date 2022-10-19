package com.chemistry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.chemistry.dto.Experiment;

import java.sql.SQLException;

public class ExperimentChooseWindow implements Screen {
    final ChemistryModelingGame game;

    private final OrthographicCamera camera;
    private final Texture background;
    private final Stage mainStage;
    private Integer experimentNum;
    private DBHandler handler = new DBHandler();
    public static Experiment choosenExperiment = new Experiment();

    public ExperimentChooseWindow(final ChemistryModelingGame game){
        this.game = game;

        try {
            experimentNum = handler.checkAvailableExperiments(); //What experiment is available
            System.out.println(experimentNum);
        } catch (Exception e){
            e.printStackTrace();
        }

        background = new Texture("main_bg.jpg");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        mainStage = new Stage();


        BitmapFont font = game.font;

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.font.getData().setScale(2);
        textFieldStyle.fontColor = new Color(255, 100, 200, 1);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.font.getData().setScale(2);
        buttonStyle.fontColor = new Color(255, 100, 200, 1);

//        final TextArea messageToUser = new TextArea("Select Experiment", textFieldStyle);
//        messageToUser.setPosition(150, 400);
//        mainStage.addActor(messageToUser);


        // In future realises add a for cycle to get experiments we need (and text for them)
        final TextButton firstExperiment = new TextButton("Первый эксперимент", buttonStyle);
        firstExperiment.setPosition(150, 360);
        mainStage.addActor(firstExperiment);

        final TextButton secondExperiment = new TextButton("Второй эксперимент", buttonStyle);
        secondExperiment.setPosition(150, 320);
        mainStage.addActor(secondExperiment);

        firstExperiment.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Clicked");
                if (experimentNum >= 1) {
                    try {
                        handler.setChoosenExperiment(Integer.toString(1));
                        game.setScreen(new ExperimentWindow(game));
                    } catch (SQLException | ClassNotFoundException throwables) {
                        throwables.printStackTrace();
                    }
                    System.out.println("WE can go further");
                } else System.out.println("???");
            }
        });

        secondExperiment.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (experimentNum >= 2){
                    System.out.println("Another one");
                } else System.out.println("That's how it should be");
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.input.setInputProcessor(mainStage);
        game.batch.setProjectionMatrix(camera.combined);
        camera.update();
        game.batch.begin();
        game.batch.draw(background,0,0);
        game.batch.end();
        mainStage.draw();
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
