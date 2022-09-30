package com.chemistry;

public class Experiment {
    private String name;
    private String texture_path;
    private String exp_id;

    public Experiment() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTexture_path() {
        return texture_path;
    }

    public void setTexture_path(String texture_path) {
        this.texture_path = texture_path;
    }

    public String getExp_id() {
        return exp_id;
    }

    public void setExp_id(String exp_id) {
        this.exp_id = exp_id;
    }
}
