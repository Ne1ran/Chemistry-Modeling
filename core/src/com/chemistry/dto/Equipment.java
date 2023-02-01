package com.chemistry.dto;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Equipment extends Rectangle {
    private String id;
    private String name;
    private Texture texture_path;
    private String x_pos;
    private String y_pos;
    private Boolean isSetOnPlace;
    private ArrayList<Substance> substancesInside = new ArrayList<>();
    private String xAfter;
    private String yAfter;

    public Equipment() {

    }

    public void addSubstance(Substance substance){
        substancesInside.add(substance);
    }
    public ArrayList<Substance> getSubstancesInside() {
        return substancesInside;
    }

    public Boolean getSetOnPlace() {
        return isSetOnPlace;
    }

    public void setSetOnPlace(Boolean setOnPlace) {
        isSetOnPlace = setOnPlace;
    }

    public String getxAfter() {
        return xAfter;
    }

    public void setxAfter(String xAfter) {
        this.xAfter = xAfter;
    }

    public String getyAfter() {
        return yAfter;
    }

    public void setyAfter(String yAfter) {
        this.yAfter = yAfter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Texture getTexture_path() {
        return texture_path;
    }

    public void setTexture_path(Texture texture_path) {
        this.texture_path = texture_path;
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
}
