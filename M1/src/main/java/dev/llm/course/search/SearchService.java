package dev.llm.course.search;

import dev.llm.course.model.dto.CourseResult;
import dev.llm.course.model.dto.StudentResult;
import java.util.List;

public interface SearchService {
    List<CourseResult> findCoursesByStudent(long studentId);
    List<StudentResult> findStudentsByCourse(long courseId);
    List<CourseResult> findTop10PopularCourses();
    String getServiceName();
}
