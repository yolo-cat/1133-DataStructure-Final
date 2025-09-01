package com.yourcompany.course.model;

public class Course {
    private long id;
    private String courseName;
    private int credits;

    // Constructors, Getters, and Setters
    public Course() {}

    public Course(long id, String courseName, int credits) {
        this.id = id;
        this.courseName = courseName;
        this.credits = credits;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
