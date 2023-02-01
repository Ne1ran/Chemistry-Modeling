package com.chemistry.dto;

import com.badlogic.gdx.math.Rectangle;

public class MenuSlot extends Rectangle {
    private Integer slotId;
    private String slotSubstanceName;
    private String substanceIdInSlot = "";
    private Boolean isThisSlotPicked = false;

    public MenuSlot() {
    }

    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public String getSlotSubstanceName() {
        return slotSubstanceName;
    }

    public void setSlotSubstanceName(String slotSubstanceName) {
        this.slotSubstanceName = slotSubstanceName;
    }

    public String getSubstanceIdInSlot() {
        return substanceIdInSlot;
    }

    public void setSubstanceIdInSlot(String substanceIdInSlot) {
        this.substanceIdInSlot = substanceIdInSlot;
    }

    public Boolean getThisSlotPicked() {
        return isThisSlotPicked;
    }

    public void setThisSlotPicked(Boolean thisSlotPicked) {
        isThisSlotPicked = thisSlotPicked;
    }
}
