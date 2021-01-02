package com.hus.student.application.object;

import com.hus.student.application.module.Transaction;

import java.util.List;

public class Class {
    private List<String> students;


    private List<Pay> pays;

    private List<Collection> collections;


    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    public List<Pay> getPays() {
        return pays;
    }

    public void setPays(List<Pay> pays) {
        this.pays = pays;
    }

    public Class() {
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }
}
