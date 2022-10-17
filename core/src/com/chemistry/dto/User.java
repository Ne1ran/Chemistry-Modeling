package com.chemistry.dto;

public class User {
    private String FIO;
    private String current_exp;
    private String exps_completed;
    private String email;
    private String password;

    public User() {
    }

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public String getCurrent_exp() {
        return current_exp;
    }

    public void setCurrent_exp(String current_exp) {
        this.current_exp = current_exp;
    }

    public String getExps_completed() {
        return exps_completed;
    }

    public void setExps_completed(String exps_completed) {
        this.exps_completed = exps_completed;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
