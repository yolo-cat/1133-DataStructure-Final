package com.yourcompany.course.model;

import java.util.Date;

/**
 * FINAL REFACTORED VERSION - Matches the correct database schema.
 */
public class Student {
    private Long studentId;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String email;

    public Student(Long studentId, String firstName, String lastName, Date dateOfBirth, String email) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }

    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
