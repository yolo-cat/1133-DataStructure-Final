package dev.llm.course.search;

import dev.llm.course.model.dto.CourseResult;
import dev.llm.course.model.dto.StudentResult;
import java.util.List;

public class SqlSearchService implements SearchService {
    @Override
    public List<CourseResult> findCoursesByStudent(long studentId) {
        return null;
    }

    @Override
    public List<StudentResult> findStudentsByCourse(long courseId) {
        return null;
    }

    @Override
    public List<CourseResult> findTop10PopularCourses() {
        return null;
    }

    @Override
    public String getServiceName() {
        return "SQL Search Service";
    }
}
