package com.yourcompany.course.model;

public class Course {
    private Long id;
    private String courseName;
    private int credits;
    private Long teacherId;

    public Course(Long id, String courseName, int credits, Long teacherId) {
        this.id = id;
        this.courseName = courseName;
        this.credits = credits;
        this.teacherId = teacherId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
}
