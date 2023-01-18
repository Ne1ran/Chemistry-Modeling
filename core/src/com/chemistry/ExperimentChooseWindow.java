package com.chemistry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.chemistry.dto.Experiment;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.chemistry.ChemistryModelingMainWindow.currentUser;

public class ExperimentChooseWindow implements Screen {
    final ChemistryModelingGame game;

    private final OrthographicCamera camera;
    private final Texture background;
    private final Stage mainStage;
    private final Integer experimentNum = 1;
    private final DBHandler handler = new DBHandler();
    public static Experiment choosenExperiment = new Experiment();
    private final ArrayList<TextButton> expButtons = new ArrayList<>();

    public ExperimentChooseWindow(final ChemistryModelingGame game) {
        this.game = game;

        background = new Texture("main_bg.jpg");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        mainStage = new Stage();


        BitmapFont font = game.font;

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = new Color(255, 100, 200, 1);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = new Color(255, 100, 200, 1);

//        final TextArea messageToUser = new TextArea("Select Experiment", textFieldStyle);
//        messageToUser.setPosition(150, 400);
//        mainStage.addActor(messageToUser);

        int possibleExperiments = Integer.parseInt(currentUser.getCurrent_exp()) + 1;
        int y_start = 500;
        int x_start = 150;
        for (int i = 1; i <= possibleExperiments; i++){
            TextButton tempBtn = null;
            try {
                tempBtn = new TextButton(handler.getExperimentNameById(i), buttonStyle);
                tempBtn.setPosition(x_start, y_start - (i * 40));
                mainStage.addActor(tempBtn);
                expButtons.add(tempBtn);
                final String exp_id = String.valueOf(i);
                tempBtn.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        try {
                            handler.setChoosenExperiment(exp_id);
                            game.setScreen(new ExperimentWindow(game));
                        } catch (SQLException | ClassNotFoundException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                });
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }

        }

        TextButton customExp = new TextButton("Создать свой эксперимент", buttonStyle);
        customExp.setPosition(650, 400);
        customExp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    game.setScreen(new CustomExperimentWindow(game));
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        mainStage.addActor(customExp);

        // In future realises add a for cycle to get experiments we need (and text for them)
//        final TextButton firstExperiment = new TextButton("Первый эксперимент", buttonStyle);
//        firstExperiment.setPosition(150, 360);
//        mainStage.addActor(firstExperiment);
//
//        final TextButton secondExperiment = new TextButton("Второй эксперимент", buttonStyle);
//        secondExperiment.setPosition(150, 320);
//        mainStage.addActor(secondExperiment);
//
//        firstExperiment.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                System.out.println("Clicked");
//                if (experimentNum >= 1) {
//                    try {
//                        handler.setChoosenExperiment(Integer.toString(1));
//                        game.setScreen(new ExperimentWindow(game));
//                    } catch (SQLException | ClassNotFoundException throwables) {
//                        throwables.printStackTrace();
//                    }
//                } else System.out.println("Something went wrong");
//            }
//        });
//
//        secondExperiment.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                if (experimentNum >= 2){
//                    System.out.println("Another one");
//                } else System.out.println("That's how it should be");
//            }
//        });
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
