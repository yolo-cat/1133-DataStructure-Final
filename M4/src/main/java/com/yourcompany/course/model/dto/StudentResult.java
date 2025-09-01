package com.yourcompany.course.model.dto;

import java.util.Date;

/**
 * 用於封裝學生查詢結果的資料傳輸物件 (DTO)。
 */
public class StudentResult {
    private String studentName;
    private String email;
    private Date enrollmentDate;

    public StudentResult(String studentName, String email, Date enrollmentDate) {
        this.studentName = studentName;
        this.email = email;
        this.enrollmentDate = enrollmentDate;
    }

    // Getters and Setters

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

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    @Override
    public String toString() {
        return "StudentResult{" +
                "studentName='" + studentName + '\'' +
                ", email='" + email + '\'' +
                ", enrollmentDate=" + enrollmentDate +
                '}';
    }
}
