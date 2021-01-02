package com.hus.student.application.object;

public class Selection {
    private int ID ;
    private String text;

    public Selection(int ID, String text) {
        this.ID = ID;
        this.text = text;
    }

    public Selection() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
