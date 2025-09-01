package com.yourcompany.course.search;

import com.yourcompany.course.model.dto.CourseResult;
import com.yourcompany.course.model.dto.StudentResult;
import com.yourcompany.course.repository.DataRepository;

import java.util.List;

/**
 * 方案 A: 完全依賴資料庫 (SQL) 進行搜尋的服務實現。
 */
public class SqlSearchService implements SearchService {

    private final DataRepository dataRepository;

    public SqlSearchService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public List<CourseResult> findCoursesByStudent(long studentId) {
        return dataRepository.findCoursesByStudentId(studentId);
    }

    @Override
    public List<StudentResult> findStudentsByCourse(long courseId) {
        return dataRepository.findStudentsByCourseId(courseId);
    }

    @Override
    public List<CourseResult> findTop10PopularCourses() {
        return dataRepository.findTop10PopularCourses();
    }

    @Override
    public String getServiceName() {
        return "SQL Search Service";
    }
}
