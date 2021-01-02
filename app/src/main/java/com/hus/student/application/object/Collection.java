package com.hus.student.application.object;

import com.hus.student.application.module.Transaction;

import java.util.List;

public class Collection  implements Transaction {


    private String title;
    private double collection ;
    private List<String> student;


    public Collection() {
    }

    public Collection(String title, double collection) {
        this.title = title;
        this.collection = collection;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getCollection() {
        return collection;
    }

    public void setCollection(double collection) {
        this.collection = collection;
    }

    public List<String> getStudent() {
        return student;
    }

    public void setStudent(List<String> student) {
        this.student = student;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
