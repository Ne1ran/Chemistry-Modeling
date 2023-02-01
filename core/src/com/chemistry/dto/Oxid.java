package com.chemistry.dto;

public class Oxid {
    private String oxid_name;
    private String name;
    private String possible_states;
    private String oxid_strength;

    public Oxid() {
    }
    public String getOxid_strength() {
        return oxid_strength;
    }
    public void setOxid_strength(String oxid_strength) {
        this.oxid_strength = oxid_strength;
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
    public String getPossible_states() {
        return possible_states;
    }
    public void setPossible_states(String possible_states) {
        this.possible_states = possible_states;
    }
}