package com.yourcompany.course.model.dto;

import java.util.Date;

/**
 * FINAL REFACTORED VERSION - Matches the correct database schema.
 */
public class StudentResult {
    private String studentFullName;
    private String email;
    private Date enrollmentDate;

    public StudentResult(String studentFullName, String email, Date enrollmentDate) {
        this.studentFullName = studentFullName;
        this.email = email;
        this.enrollmentDate = enrollmentDate;
    }

    // Getters and Setters
    public String getStudentFullName() { return studentFullName; }
    public void setStudentFullName(String studentFullName) { this.studentFullName = studentFullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Date getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Date enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    @Override
    public String toString() {
        return "StudentResult{" +
                "studentFullName='" + studentFullName + '\'' +
                ", email='" + email + '\'' +
                ", enrollmentDate=" + enrollmentDate +
                '}';
    }
}
