package com.hus.student.application.object;

import com.hus.student.application.module.Transaction;

public class Pay implements Transaction {

    private String title;

    private double amountOfMoney;


    public void setTitle(String title) {
        this.title = title;

    }

    public Pay(String title, double amountOfMoney) {
        this.title = title;
        this.amountOfMoney = amountOfMoney;
    }

    public Pay() {
    }

    public double getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(double amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }

    @Override
    public String getTitle() {
        return this.title;
    }
}
