package com.chemistry;

public class Foundation {
    private String foundation_name;
    private String name;
    private String found_state_min;
    private String found_state_max;
    private String electrochem_pos;

    public Foundation() {
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

    public String getFound_state_min() {
        return found_state_min;
    }

    public void setFound_state_min(String found_state_min) {
        this.found_state_min = found_state_min;
    }

    public String getFound_state_max() {
        return found_state_max;
    }

    public void setFound_state_max(String found_state_max) {
        this.found_state_max = found_state_max;
    }

    public String getElectrochem_pos() {
        return electrochem_pos;
    }

    public void setElectrochem_pos(String electrochem_pos) {
        this.electrochem_pos = electrochem_pos;
    }
}
