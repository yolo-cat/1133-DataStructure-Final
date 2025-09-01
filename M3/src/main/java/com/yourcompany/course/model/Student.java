package com.yourcompany.course.model;

public class Student {
    private long id;
    private String studentName;
    private String email;

    // Constructors, Getters, and Setters
    public Student() {}

    public Student(long id, String studentName, String email) {
        this.id = id;
        this.studentName = studentName;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
