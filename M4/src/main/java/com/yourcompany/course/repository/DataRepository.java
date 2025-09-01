package com.yourcompany.course.repository;

import com.yourcompany.course.model.Course;
import com.yourcompany.course.model.Enrollment;
import com.yourcompany.course.model.Student;
import com.yourcompany.course.model.dto.CourseResult;
import com.yourcompany.course.model.dto.StudentResult;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRepository {

    private final DataSource dataSource;

    public DataRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // --- Methods for InMemorySearchService Data Loading ---

    public List<Student> findAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id, full_name, birth_date, email FROM students";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                students.add(new Student(rs.getLong("id"), rs.getString("full_name"), rs.getDate("birth_date"), rs.getString("email")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public List<Course> findAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT id, course_name, credits, teacher_id FROM courses";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                courses.add(new Course(rs.getLong("id"), rs.getString("course_name"), rs.getInt("credits"), rs.getLong("teacher_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public List<Enrollment> findAllEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT id, student_id, course_id, enrollment_date FROM enrollments";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                enrollments.add(new Enrollment(rs.getLong("id"), rs.getLong("student_id"), rs.getLong("course_id"), rs.getTimestamp("enrollment_date")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }

    // --- Methods for SqlSearchService ---

    public List<CourseResult> findCoursesByStudentId(long studentId) {
        List<CourseResult> results = new ArrayList<>();
        String sql = "SELECT c.course_name, c.credits, e.enrollment_date FROM enrollments e JOIN courses c ON e.course_id = c.id WHERE e.student_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new CourseResult(rs.getString("course_name"), rs.getInt("credits"), rs.getTimestamp("enrollment_date")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<StudentResult> findStudentsByCourseId(long courseId) {
        List<StudentResult> results = new ArrayList<>();
        String sql = "SELECT s.full_name, s.email, e.enrollment_date FROM enrollments e JOIN students s ON e.student_id = s.id WHERE e.course_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new StudentResult(rs.getString("full_name"), rs.getString("email"), rs.getTimestamp("enrollment_date")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<CourseResult> findTop10PopularCourses() {
        List<CourseResult> results = new ArrayList<>();
        String sql = "SELECT c.course_name, c.credits, COUNT(e.student_id) AS enrollment_count FROM enrollments e JOIN courses c ON e.course_id = c.id GROUP BY c.id, c.course_name, c.credits ORDER BY enrollment_count DESC LIMIT 10";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(new CourseResult(rs.getString("course_name"), rs.getInt("credits"), rs.getLong("enrollment_count")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
