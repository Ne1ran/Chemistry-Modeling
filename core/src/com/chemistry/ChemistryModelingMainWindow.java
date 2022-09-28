package com.chemistry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ChemistryModelingMainWindow implements Screen {
    final ChemistryModelingGame game;

    private final OrthographicCamera camera;
    private final Texture background;
    private final Stage stage;

    public ChemistryModelingMainWindow(ChemistryModelingGame game) {
        this.game = game;

        background = new Texture("background_main.png");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        BitmapFont font = new BitmapFont();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.font.getData().setScale(2);
        buttonStyle.fontColor = new Color(255, 100, 200, 1);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.font.getData().setScale(2);
        textFieldStyle.fontColor = new Color(255, 100, 200, 1);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Button startAsNewUser = new TextButton("Start", buttonStyle);
        stage.addActor(startAsNewUser);
        startAsNewUser.setPosition(40, 480);

        Button startAsRegistratedUser = new TextButton("Continue", buttonStyle);
        stage.addActor(startAsRegistratedUser);
        startAsRegistratedUser.setPosition(40, 440);

        Button settings = new TextButton("Settings", buttonStyle);
        stage.addActor(settings);
        settings.setPosition(40, 400);

        Button exit = new TextButton("Exit", buttonStyle);
        stage.addActor(exit);
        exit.setPosition(40, 360);

        final TextField userName = new TextField("", textFieldStyle);
        userName.setMessageText("check");
        userName.setPosition(355, 440);
        stage.addActor(userName);

        startAsNewUser.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("You tried to start new game");
                String test = userName.getText();
                System.out.println(test);
            }
        });

        startAsRegistratedUser.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor){
                System.out.println("You tried to continue a game");
            }
        });

        settings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("You pressed Settings btn");
            }
        });

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.exit(1);
            }
        });

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        game.batch.setProjectionMatrix(camera.combined);
        camera.update();
        game.batch.begin();
        game.batch.draw(background,0,0);
        game.batch.end();
        stage.draw();
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
