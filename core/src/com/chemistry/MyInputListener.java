package com.chemistry;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import static com.chemistry.ExperimentWindow.*;


public class MyInputListener implements InputProcessor {

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        x_pos = screenX - 5;
        y_pos = screenY - 5;
        mouseSpawnerRect.setX(x_pos);
        mouseSpawnerRect.setY(y_pos);
        startSpawn = true;
        System.out.println(x_pos + " - " + y_pos);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        x_pos = -100;
        y_pos = -100;
        mouseSpawnerRect.setX(x_pos);
        mouseSpawnerRect.setY(y_pos);
        startSpawn = false;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
