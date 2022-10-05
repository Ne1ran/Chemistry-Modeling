package com.chemistry;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Substance extends Rectangle {

    private String subId;
    private String x_pos;
    private String y_pos;
    private Texture texture_path;
    private String foundation;
    private String oxid;
    private String name;
    private Texture smallTexturePath;

    public Substance() {
    }

    public Texture getSmallTexturePath() {
        return smallTexturePath;
    }

    public void setSmallTexturePath(Texture smallTexturePath) {
        this.smallTexturePath = smallTexturePath;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
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
