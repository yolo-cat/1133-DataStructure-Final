package com.yourcompany.course.model;

import java.util.Date;

/**
 * FINAL REFACTORED VERSION - Matches the correct database schema.
 */
public class Enrollment {
    private Long enrollmentId; // Assuming a primary key for the enrollment itself
    private Long studentId;
    private Long courseId;
    private Date enrollmentDate;

    public Enrollment(Long enrollmentId, Long studentId, Long courseId, Date enrollmentDate) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
    }

    // Getters and Setters
    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Date getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Date enrollmentDate) { this.enrollmentDate = enrollmentDate; }
}
