package com.yourcompany.course.search;

import com.yourcompany.course.model.dto.CourseResult;
import com.yourcompany.course.model.dto.StudentResult;

import java.util.Collections;
import java.util.List;

/**
 * 方案A: 資料庫驅動的搜尋服務實現 (Placeholder)。
 */
public class SqlSearchService implements SearchService {

    @Override
    public List<CourseResult> findCoursesByStudent(long studentId) {
        // Placeholder: 實際應查詢資料庫
        System.out.println("SQL Service: Finding courses for student " + studentId);
        return Collections.emptyList();
    }

    @Override
    public List<StudentResult> findStudentsByCourse(long courseId) {
        // Placeholder: 實際應查詢資料庫
        System.out.println("SQL Service: Finding students for course " + courseId);
        return Collections.emptyList();
    }

    @Override
    public List<CourseResult> findTop10PopularCourses() {
        // Placeholder: 實際應查詢資料庫
        System.out.println("SQL Service: Finding top 10 popular courses");
        return Collections.emptyList();
    }

    @Override
    public String getServiceName() {
        return "SQL Search Service";
    }
}
