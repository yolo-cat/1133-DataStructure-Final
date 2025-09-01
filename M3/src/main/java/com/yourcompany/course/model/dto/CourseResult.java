package com.yourcompany.course.model.dto;

import java.util.Date;

public class CourseResult {
    private String courseName;
    private int credits;
    private Date enrollmentDate;
    private long enrollmentCount;

    public CourseResult(String courseName, int credits, Date enrollmentDate) {
        this.courseName = courseName;
        this.credits = credits;
        this.enrollmentDate = enrollmentDate;
    }

    public CourseResult(String courseName, int credits, long enrollmentCount) {
        this.courseName = courseName;
        this.credits = credits;
        this.enrollmentCount = enrollmentCount;
    }

    // Getters and Setters
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

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public long getEnrollmentCount() {
        return enrollmentCount;
    }

    public void setEnrollmentCount(long enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }
}
