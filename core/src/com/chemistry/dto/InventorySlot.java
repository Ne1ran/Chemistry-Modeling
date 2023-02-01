package com.chemistry.dto;

import com.badlogic.gdx.math.Rectangle;

public class InventorySlot extends Rectangle {
    private Integer slotId;
    private String slotTexture;
    private Substance substanceInSlot;
    private Boolean isThisSlotPicked = false;

    public InventorySlot() {
    }

    public Boolean getThisSlotPicked() {
        return isThisSlotPicked;
    }

    public void setThisSlotPicked(Boolean thisSlotPicked) {
        isThisSlotPicked = thisSlotPicked;
    }

    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public Substance getSubstanceInSlot() {
        return substanceInSlot;
    }

    public void setSubstanceInSlot(Substance substanceInSlot) {
        this.substanceInSlot = substanceInSlot;
    }

    public String getSlotTexture() {
        return slotTexture;
    }

    public void setSlotTexture(String slotTexture) {
        this.slotTexture = slotTexture;
    }
}
