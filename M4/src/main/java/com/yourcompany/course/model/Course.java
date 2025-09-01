package com.yourcompany.course.model;

/**
 * FINAL REFACTORED VERSION - Matches the correct database schema.
 */
public class Course {
    private Long courseId;
    private String courseName;
    private String courseDescription;
    private int credits;
    private Long teacherId;

    public Course(Long courseId, String courseName, String courseDescription, int credits, Long teacherId) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.credits = credits;
        this.teacherId = teacherId;
    }

    // Getters and Setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCourseDescription() { return courseDescription; }
    public void setCourseDescription(String courseDescription) { this.courseDescription = courseDescription; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
}
