package com.hus.student.application.object;

import java.util.UUID;

public class Person {
    private String name;
    private String UUID;


    public Person() {
    }

    public Person(String name, String UUID) {
        this.name = name;
        this.UUID = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getUUID() {
        if (UUID == null) {
            return java.util.UUID.randomUUID().toString();
        }
        return UUID;
    }
}
