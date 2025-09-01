package com.yourcompany.course.model;

import java.util.Date;

public class Student {
    private Long id;
    private String fullName;
    private Date birthDate;
    private String email;

    public Student(Long id, String fullName, Date birthDate, String email) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
