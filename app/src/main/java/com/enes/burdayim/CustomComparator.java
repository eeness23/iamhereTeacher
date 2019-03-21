package com.enes.burdayim;

import java.util.Comparator;

public class CustomComparator implements Comparator<Student> {
    @Override
    public int compare(Student o1, Student o2) {
        return o1.getNumber().compareTo(o2.getNumber());
    }
}
