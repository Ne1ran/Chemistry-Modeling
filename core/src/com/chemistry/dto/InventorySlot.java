package com.chemistry.dto;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class InventorySlot extends Rectangle {
    private Integer slotId;
    private Texture slotTexture;
    private String substanceIdInSlot = "";
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

    public String getSubstanceIdInSlot() {
        return substanceIdInSlot;
    }

    public void setSubstanceIdInSlot(String substanceIdInSlot) {
        this.substanceIdInSlot = substanceIdInSlot;
    }

    public Texture getSlotTexture() {
        return slotTexture;
    }

    public void setSlotTexture(Texture slotTexture) {
        this.slotTexture = slotTexture;
    }
}
