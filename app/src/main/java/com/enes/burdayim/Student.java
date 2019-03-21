package com.enes.burdayim;



public class Student {
    private String name;
    private String number;
    private String level;
    private String key;

    public Student() { }

    public Student(String number, String name, String level) {
        this.name = name;
        this.number = number;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
