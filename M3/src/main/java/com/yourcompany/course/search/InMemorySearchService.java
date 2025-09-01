package com.yourcompany.course.search;

import com.yourcompany.course.model.Course;
import com.yourcompany.course.model.Enrollment;
import com.yourcompany.course.model.Student;
import com.yourcompany.course.model.dto.CourseResult;
import com.yourcompany.course.model.dto.StudentResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemorySearchService implements SearchService {

    // 主索引：用於功能1，從學生ID快速找到選課紀錄
    private Map<Long, List<Enrollment>> studentEnrollmentIndex;

    // 主索引：用於功能2/3，從課程ID快速找到選課紀錄
    private Map<Long, List<Enrollment>> courseEnrollmentIndex;

    // 輔助資料：用於快速獲取學生/課程的詳細資訊，避免遍歷
    private Map<Long, Student> studentDetailsCache;
    private Map<Long, Course> courseDetailsCache;

    public InMemorySearchService() {
        // 在此處初始化資料庫連線或 Repository
    }

    public void loadData() {
        // --- 模擬資料生成 ---
        List<Student> allStudents = new ArrayList<>();
        allStudents.add(new Student(1L, "Alice", "alice@example.com"));
        allStudents.add(new Student(2L, "Bob", "bob@example.com"));

        List<Course> allCourses = new ArrayList<>();
        allCourses.add(new Course(101L, "Data Structures", 3));
        allCourses.add(new Course(102L, "Algorithms", 4));

        List<Enrollment> allEnrollments = new ArrayList<>();
        allEnrollments.add(new Enrollment(1L, 101L, new Date()));
        allEnrollments.add(new Enrollment(1L, 102L, new Date()));
        allEnrollments.add(new Enrollment(2L, 101L, new Date()));
        // --- 模擬資料結束 ---

        // 5. 使用 Stream API 建構索引
        this.studentEnrollmentIndex = allEnrollments.stream()
            .collect(Collectors.groupingBy(Enrollment::getStudentId));

        this.courseEnrollmentIndex = allEnrollments.stream()
            .collect(Collectors.groupingBy(Enrollment::getCourseId));

        // 6. 建構輔助資料快取 (Cache)
        this.studentDetailsCache = allStudents.stream()
            .collect(Collectors.toMap(Student::getId, student -> student));

        this.courseDetailsCache = allCourses.stream()
            .collect(Collectors.toMap(Course::getId, course -> course));
    }

    @Override
    public List<CourseResult> findCoursesByStudent(long studentId) {
        List<Enrollment> enrollments = studentEnrollmentIndex.get(studentId);
        if (enrollments == null || enrollments.isEmpty()) {
            return Collections.emptyList();
        }

        return enrollments.stream()
            .map(enrollment -> {
                Course course = courseDetailsCache.get(enrollment.getCourseId());
                return new CourseResult(course.getCourseName(), course.getCredits(), enrollment.getEnrollmentDate());
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<StudentResult> findStudentsByCourse(long courseId) {
        List<Enrollment> enrollments = courseEnrollmentIndex.get(courseId);
        if (enrollments == null || enrollments.isEmpty()) {
            return Collections.emptyList();
        }

        return enrollments.stream()
            .map(enrollment -> {
                Student student = studentDetailsCache.get(enrollment.getStudentId());
                return new StudentResult(student.getStudentName(), student.getEmail(), enrollment.getEnrollmentDate());
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<CourseResult> findTop10PopularCourses() {
        return courseEnrollmentIndex.entrySet().stream()
            .sorted(Map.Entry.<Long, List<Enrollment>>comparingByValue(Comparator.comparingInt(List::size)).reversed())
            .limit(10)
            .map(entry -> {
                Course course = courseDetailsCache.get(entry.getKey());
                long enrollmentCount = entry.getValue().size();
                return new CourseResult(course.getCourseName(), course.getCredits(), enrollmentCount);
            })
            .collect(Collectors.toList());
    }
}
