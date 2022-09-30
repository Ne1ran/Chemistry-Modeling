package com.chemistry;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import static com.chemistry.ExperimentChooseWindow.choosenExperiment;

public class ExperimentWindow implements Screen {
    final ChemistryModelingGame game;
    private final Texture experimentBackground;
    private DBHandler handler = new DBHandler();
    private final OrthographicCamera camera;

    public ExperimentWindow(ChemistryModelingGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        experimentBackground = new Texture(choosenExperiment.getTexture_path());
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
        game.batch.end();
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
