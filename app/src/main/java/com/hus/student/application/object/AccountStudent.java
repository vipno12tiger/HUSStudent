package com.hus.student.application.object;

public class AccountStudent {
    private String msv;
    private String email;
    private String password;
    private int root;


    public AccountStudent(String msv, String email, String password, int root) {
        this.msv = msv;
        this.email = email;
        this.password = password;
        this.root = root;
    }

    public AccountStudent() {
    }

    public String getMsv() {
        return msv;
    }

    public void setMsv(String msv) {
        this.msv = msv;
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

    public int getRoot() {
        return root;
    }

    public void setRoot(int root) {
        this.root = root;
    }
}
