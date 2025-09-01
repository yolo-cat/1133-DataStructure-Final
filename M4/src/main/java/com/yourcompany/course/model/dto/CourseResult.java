package com.yourcompany.course.model.dto;

import java.util.Date;

/**
 * 用於封裝課程查詢結果的資料傳輸物件 (DTO)。
 */
public class CourseResult {
    private String courseName;
    private int credits;
    private Date enrollmentDate; // 可選
    private long enrollmentCount; // 用於熱門課程功能

    public CourseResult(String courseName, int credits, long enrollmentCount) {
        this.courseName = courseName;
        this.credits = credits;
        this.enrollmentCount = enrollmentCount;
    }

    public CourseResult(String courseName, int credits, Date enrollmentDate) {
        this.courseName = courseName;
        this.credits = credits;
        this.enrollmentDate = enrollmentDate;
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

    @Override
    public String toString() {
        return "CourseResult{" +
                "courseName='" + courseName + '\'' +
                ", credits=" + credits +
                (enrollmentDate != null ? ", enrollmentDate=" + enrollmentDate : "") +
                (enrollmentCount > 0 ? ", enrollmentCount=" + enrollmentCount : "") +
                '}';
    }
}
