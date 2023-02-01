package com.chemistry.dto;

public class Foundation {
    private String foundation_name;
    private String name;
    private String possible_states;
    private String electrochem_pos;

    public Foundation() {
    }
    public String getPossible_states() {
        return possible_states;
    }
    public void setPossible_states(String possible_states) {
        this.possible_states = possible_states;
    }
    public String getFoundation_name() {
        return foundation_name;
    }
    public void setFoundation_name(String foundation_name) {
        this.foundation_name = foundation_name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getElectrochem_pos() {
        return electrochem_pos;
    }
    public void setElectrochem_pos(String electrochem_pos) {
        this.electrochem_pos = electrochem_pos;
    }
}