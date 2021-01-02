package com.hus.student.application.object;

public class Account {
    private String user,password,root,codeClass;

    public Account() {
        
    }

    public Account(String user, String password, String root, String codeClass) {
        this.user = user;
        this.password = password;
        this.root = root;
        this.codeClass = codeClass;
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

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getCodeClass() {
        return codeClass;
    }

    public void setCodeClass(String codeClass) {
        this.codeClass = codeClass;
    }
}
