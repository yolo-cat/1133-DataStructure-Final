package com.yourcompany.course.model;

/**
 * FINAL REFACTORED VERSION - Matches the correct database schema.
 */
public class Teacher {
    private Long teacherId;
    private String name;
    private String email;

    public Teacher(Long teacherId, String name, String email) {
        this.teacherId = teacherId;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
