package com.hus.student.application.object;

import java.util.ArrayList;
import java.util.List;

public class Teacher {
    private String email,user,password;

    private List<AccountStudent> student;

    public Teacher(String email, String user, String password) {
        this.email = email;
        this.user = user;
        this.password = password;
    }

    public Teacher() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<AccountStudent> getStudent() {
        if(student==null){
            return new ArrayList<>();
        }
        return student;
    }

    public void setStudent(List<AccountStudent> student) {
        this.student = student;
    }
}
