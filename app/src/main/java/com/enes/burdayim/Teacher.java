package com.enes.burdayim;


import java.util.ArrayList;
import java.util.List;

public class Teacher {
    private String username;
    private String password;
    private List<String> course;
    private String name;
    private String qrCode;

    public Teacher() { }

    public Teacher(String username, String password, List<String> course, String name, String qrCode) {
        this.username = username;
        this.password = password;
        this.course = course;
        this.name = name;
        this.qrCode = qrCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getCourse() {
        return course;
    }

    public void setCourse(List<String> course) {
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

}
