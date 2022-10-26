package com.chemistry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.chemistry.dto.User;

import java.sql.SQLException;

public class ChemistryModelingMainWindow implements Screen {
    final ChemistryModelingGame game;
    private DBHandler handler = new DBHandler();

    private final OrthographicCamera camera;
    private final Texture background;
    private final Stage stage;
    private final Stage stageNewUser;
    private final Stage stageContinue;
    public String message = "";

    public static User currentUser = new User();

    boolean stageNewUserStartDraw = false;
    boolean stageContinueStartDraw = false;

    private BitmapFont startingScreenFont;

    public ChemistryModelingMainWindow(final ChemistryModelingGame game) {
        this.game = game;

        startingScreenFont = game.font;

        background = new Texture("main_bg.jpg");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = startingScreenFont;

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = startingScreenFont;
        textFieldStyle.fontColor = new Color(0.7f, 0.7f, 0.7f, 1);

        stage = new Stage();

        stageNewUser = new Stage();

        stageContinue = new Stage();

        Gdx.input.setInputProcessor(stage);

        final Button cancelContinue = new TextButton("Отменить?", buttonStyle);
        stageContinue.addActor(cancelContinue);
        cancelContinue.setPosition(400, 360);

        final Button cancelStart = new TextButton("Отменить?", buttonStyle);
        stageNewUser.addActor(cancelStart);
        cancelStart.setPosition(400, 320);

        final Button startAsNewUser = new TextButton("Зарегистрироваться", buttonStyle);
        stage.addActor(startAsNewUser);
        startAsNewUser.setPosition(40, 480);

        Button startAsRegistratedUser = new TextButton("Продолжить", buttonStyle);
        stage.addActor(startAsRegistratedUser);
        startAsRegistratedUser.setPosition(40, 440);

        Button settings = new TextButton("Настройки", buttonStyle);
        stage.addActor(settings);
        settings.setPosition(40, 400);

        Button exit = new TextButton("Выход", buttonStyle);
        stage.addActor(exit);
        exit.setPosition(40, 360);

        final TextField userName = new TextField("", textFieldStyle);
        userName.setMessageText("Введите ваше ФИО");
        userName.setPosition(400, 480);
        userName.setWidth(450F);
        stageNewUser.addActor(userName);

        final TextField userPassword = new TextField("", textFieldStyle);
        userPassword.setMessageText("Введите пароль");
        userPassword.setPosition(400, 440);
        userPassword.setWidth(450);
        stageNewUser.addActor(userPassword);

        final TextField userEmail = new TextField("", textFieldStyle);
        userEmail.setMessageText("Введите электронную почту");
        userEmail.setPosition(400, 400);
        userEmail.setWidth(450);
        stageNewUser.addActor(userEmail);

        final TextField userEmailContinue = new TextField("", textFieldStyle);
        userEmailContinue.setMessageText("Введите электронную почту");
        userEmailContinue.setPosition(400, 480);
        userEmailContinue.setWidth(450);
        stageContinue.addActor(userEmailContinue);

        final TextField userPasswordContinue = new TextField("", textFieldStyle);
        userPasswordContinue.setMessageText("Введите пароль");
        userPasswordContinue.setPosition(400, 440);
        userPasswordContinue.setWidth(450);
        stageContinue.addActor(userPasswordContinue);

        final Button confirmRegistration = new TextButton("Подтвердить регистрацию?", buttonStyle);
        confirmRegistration.setPosition(400, 360);
        stageNewUser.addActor(confirmRegistration);

        final Button authorizeBtn = new TextButton("Авторизироваться", buttonStyle);
        authorizeBtn.setPosition(400, 400);
        stageContinue.addActor(authorizeBtn);


        startAsNewUser.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                message = "Введите ваши персональные данные";
                stageNewUserStartDraw = true;
                stageContinueStartDraw = false;
                Gdx.input.setInputProcessor(stageNewUser);
            }
        });

        confirmRegistration.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Name " + userName.getText() + "\n Password " + userPassword.getText() +
                        "\n Email " + userEmail.getText());
                User newUser = new User();
                newUser.setFIO(userName.getText().trim());
                newUser.setEmail(userEmail.getText().trim());
                newUser.setPassword(userPassword.getText().trim());
                newUser.setCurrent_exp("1");
                newUser.setExps_completed("0");
                try {
                    handler.addNewUser(newUser);
                    message = "Вы успешно зарегистрировались!";
                } catch (SQLException | ClassNotFoundException throwables) {
                    throwables.printStackTrace();
                }
                userPassword.setText("");
                userEmail.setText("");
                userName.setText("");
                stageNewUserStartDraw = false;
                Gdx.input.setInputProcessor(stage);
            }
        });

        authorizeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    if (handler.authorize(userEmailContinue.getText().trim(), userPasswordContinue.getText().trim())){
                        game.setScreen(new ExperimentChooseWindow(game));
                        stage.dispose();
                        stageNewUser.dispose();
                        stageContinue.dispose();
                    } else message = "Пользователь не найден";
                } catch (SQLException | ClassNotFoundException throwables) {
                    throwables.printStackTrace();
                }

            }
        });

        startAsRegistratedUser.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor){
                message = "Вы попытались продолжить работу";
               stageContinueStartDraw = true;
               stageNewUserStartDraw = false;
               Gdx.input.setInputProcessor(stageContinue);
            }
        });

        settings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                message = "Кнопка Настройки пока не работает";
            }
        });

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.exit(1);
            }
        });

        cancelContinue.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                message = "";
                stageContinueStartDraw = false;
                stageNewUserStartDraw = false;
                Gdx.input.setInputProcessor(stage);
            }
        });

        cancelStart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                message = "";
                stageContinueStartDraw = false;
                stageNewUserStartDraw = false;
                Gdx.input.setInputProcessor(stage);
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
        startingScreenFont.draw(game.batch, message, 400, 300);
        game.batch.end();
        stage.draw();
        if (stageNewUserStartDraw){
            stageNewUser.draw();
        }
        if (stageContinueStartDraw){
            stageContinue.draw();
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
