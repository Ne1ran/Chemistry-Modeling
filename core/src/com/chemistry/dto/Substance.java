package com.chemistry.dto;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Substance extends Rectangle {
    private String subId;
    private String x_pos;
    private String y_pos;
    private Texture texture;
    private String foundation;
    private String oxid;
    private String name;
    private String found_amount;
    private String oxid_amount;
    private String substanceNameInGame = "Ничего";
    private String substanceType;
    private String unstable_type;
    public Substance() {
    }
    public String getUnstable_type() {
        return unstable_type;
    }
    public void setUnstable_type(String unstable_type) {
        this.unstable_type = unstable_type;
    }
    public String getSubstanceType() {
        return substanceType;
    }
    public void setSubstanceType(String substanceType) {
        this.substanceType = substanceType;
    }
    public String getSubstanceNameInGame() {
        return substanceNameInGame;
    }
    public void setSubstanceNameInGame(String substanceNameInGame) {
        this.substanceNameInGame = substanceNameInGame;
    }
    public String getFound_amount() {
        return found_amount;
    }
    public void setFound_amount(String found_amount) {
        this.found_amount = found_amount;
    }
    public String getOxid_amount() {
        return oxid_amount;
    }
    public void setOxid_amount(String oxid_amount) {
        this.oxid_amount = oxid_amount;
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
    public Texture getTexture() {
        return texture;
    }
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}