package com.chemistry;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Substance extends Rectangle {

    String x_pos;
    String y_pos;
    Texture texture_path;
    String foundation;
    String oxid;
    String name;

    public Substance() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoundation() {
        return foundation;
    }

    public void setFoundation(String foundation) {
        this.foundation = foundation;
    }

    public String getOxid() {
        return oxid;
    }

    public void setOxid(String oxid) {
        this.oxid = oxid;
    }

    public String getX_pos() {
        return x_pos;
    }

    public void setX_pos(String x_pos) {
        this.x_pos = x_pos;
    }

    public String getY_pos() {
        return y_pos;
    }

    public void setY_pos(String y_pos) {
        this.y_pos = y_pos;
    }

    public Texture getTexture_path() {
        return texture_path;
    }

    public void setTexture_path(Texture texture_path) {
        this.texture_path = texture_path;
    }
}
