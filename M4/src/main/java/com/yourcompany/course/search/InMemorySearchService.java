package com.yourcompany.course.search;

import com.yourcompany.course.model.Course;
import com.yourcompany.course.model.Enrollment;
import com.yourcompany.course.model.Student;
import com.yourcompany.course.model.dto.CourseResult;
import com.yourcompany.course.model.dto.StudentResult;
import com.yourcompany.course.repository.DataRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FINAL REFACTORED VERSION - Matches the correct database schema.
 */
public class InMemorySearchService implements SearchService {

    private final DataRepository dataRepository;

    // Indices and Caches
    private Map<Long, List<Enrollment>> studentEnrollmentIndex;
    private Map<Long, List<Enrollment>> courseEnrollmentIndex;
    private Map<Long, Student> studentDetailsCache;
    private Map<Long, Course> courseDetailsCache;

    private long memoryUsageBytes = 0;

    public InMemorySearchService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public void loadData() {
        clearData();
        long memoryBefore = getMemoryUsage();

        List<Student> allStudents = dataRepository.findAllStudents();
        List<Course> allCourses = dataRepository.findAllCourses();
        List<Enrollment> allEnrollments = dataRepository.findAllEnrollments();

        studentEnrollmentIndex = allEnrollments.stream()
                .collect(Collectors.groupingBy(Enrollment::getStudentId));

        courseEnrollmentIndex = allEnrollments.stream()
                .collect(Collectors.groupingBy(Enrollment::getCourseId));

        studentDetailsCache = allStudents.stream()
                .collect(Collectors.toMap(Student::getStudentId, student -> student));

        courseDetailsCache = allCourses.stream()
                .collect(Collectors.toMap(Course::getCourseId, course -> course));
        
        long memoryAfter = getMemoryUsage();
        this.memoryUsageBytes = memoryAfter - memoryBefore;
    }

    private void clearData() {
        studentEnrollmentIndex = null;
        courseEnrollmentIndex = null;
        studentDetailsCache = null;
        courseDetailsCache = null;
        System.gc();
    }

    private long getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public long getMemoryUsageBytes() {
        return memoryUsageBytes;
    }

    @Override
    public List<CourseResult> findCoursesByStudent(long studentId) {
        if (studentEnrollmentIndex == null) return Collections.emptyList();

        List<Enrollment> enrollments = studentEnrollmentIndex.getOrDefault(studentId, Collections.emptyList());

        return enrollments.stream()
                .map(enrollment -> {
                    Course course = courseDetailsCache.get(enrollment.getCourseId());
                    if (course == null) return null;
                    return new CourseResult(course.getCourseName(), course.getCredits(), enrollment.getEnrollmentDate());
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentResult> findStudentsByCourse(long courseId) {
        if (courseEnrollmentIndex == null) return Collections.emptyList();

        List<Enrollment> enrollments = courseEnrollmentIndex.getOrDefault(courseId, Collections.emptyList());

        return enrollments.stream()
                .map(enrollment -> {
                    Student student = studentDetailsCache.get(enrollment.getStudentId());
                    if (student == null) return null;
                    String fullName = student.getFirstName() + " " + student.getLastName();
                    return new StudentResult(fullName, student.getEmail(), enrollment.getEnrollmentDate());
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResult> findTop10PopularCourses() {
        if (courseEnrollmentIndex == null) return Collections.emptyList();

        return courseEnrollmentIndex.entrySet().stream()
                .sorted(Map.Entry.<Long, List<Enrollment>>comparingByValue(Comparator.comparingInt(List::size)).reversed())
                .limit(10)
                .map(entry -> {
                    Course course = courseDetailsCache.get(entry.getKey());
                    if (course == null) return null;
                    long enrollmentCount = entry.getValue().size();
                    return new CourseResult(course.getCourseName(), course.getCredits(), enrollmentCount);
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    public String getServiceName() {
        return "In-Memory Search Service";
    }
}
