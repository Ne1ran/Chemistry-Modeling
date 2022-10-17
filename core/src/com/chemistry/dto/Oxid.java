package com.chemistry.dto;

public class Oxid {
    private String oxid_name;
    private String name;
    private String oxid_state_min;
    private String oxid_state_max;

    public Oxid() {
    }

    public String getOxid_name() {
        return oxid_name;
    }

    public void setOxid_name(String oxid_name) {
        this.oxid_name = oxid_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOxid_state_min() {
        return oxid_state_min;
    }

    public void setOxid_state_min(String oxid_state_min) {
        this.oxid_state_min = oxid_state_min;
    }

    public String getOxid_state_max() {
        return oxid_state_max;
    }

    public void setOxid_state_max(String oxid_state_max) {
        this.oxid_state_max = oxid_state_max;
    }
}
