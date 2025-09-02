package com.yourcompany.course.repository;

import com.yourcompany.course.model.Course;
import com.yourcompany.course.model.Enrollment;
import com.yourcompany.course.model.Student;
import com.yourcompany.course.model.Teacher;
import com.yourcompany.course.model.dto.CourseResult;
import com.yourcompany.course.model.dto.StudentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FINAL REFACTORED VERSION - Matches the correct database schema.
 */
public class DataRepository {

    private static final Logger logger = LoggerFactory.getLogger(DataRepository.class);
    private final DataSource dataSource;

    public DataRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setupDatabase() {
        String schema;
        try (InputStream in = getClass().getResourceAsStream("/db_schema.sql")) {
            if (in == null) throw new RuntimeException("db_schema.sql not found in resources.");
            schema = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read db_schema.sql", e);
        }

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            for (String sqlStatement : schema.split(";")) {
                if (!sqlStatement.trim().isEmpty()) {
                    stmt.execute(sqlStatement);
                }
            }
            logger.info("Database schema created or verified successfully.");
        } catch (SQLException e) {
            logger.error("Failed to execute schema script: {}", e.getMessage());
        }
    }

    public void batchInsertTeachers(List<Teacher> teachers) {
        String sql = "INSERT INTO Teacher (name, email) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (Teacher teacher : teachers) {
                pstmt.setString(1, teacher.getName());
                pstmt.setString(2, teacher.getEmail());
                if (pstmt.executeUpdate() > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            teacher.setTeacherId(generatedKeys.getLong(1));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Insert for Teacher failed.", e);
        }
    }

    public void batchInsertStudents(List<Student> students) {
        String sql = "INSERT INTO Student (first_name, last_name, date_of_birth, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (Student student : students) {
                pstmt.setString(1, student.getFirstName());
                pstmt.setString(2, student.getLastName());
                pstmt.setDate(3, new java.sql.Date(student.getDateOfBirth().getTime()));
                pstmt.setString(4, student.getEmail());
                if (pstmt.executeUpdate() > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            student.setStudentId(generatedKeys.getLong(1));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Insert for Student failed.", e);
        }
    }

    public void batchInsertCourses(List<Course> courses) {
        String sql = "INSERT INTO Course (course_name, course_description, credits, teacher_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (Course course : courses) {
                pstmt.setString(1, course.getCourseName());
                pstmt.setString(2, course.getCourseDescription());
                pstmt.setInt(3, course.getCredits());
                pstmt.setLong(4, course.getTeacherId());
                if (pstmt.executeUpdate() > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            course.setCourseId(generatedKeys.getLong(1));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Insert for Course failed.", e);
        }
    }

    public void batchInsertEnrollments(List<Enrollment> enrollments) {
        String sql = "INSERT INTO Enrollment (student_id, course_id, enrollment_date) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Enrollment enrollment : enrollments) {
                pstmt.setLong(1, enrollment.getStudentId());
                pstmt.setLong(2, enrollment.getCourseId());
                pstmt.setTimestamp(3, new Timestamp(enrollment.getEnrollmentDate().getTime()));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            logger.error("Batch insert for Enrollment failed.", e);
        }
    }

    public List<CourseResult> findCoursesByStudentId(long studentId) {
        List<CourseResult> results = new ArrayList<>();
        String sql = "SELECT c.course_name, c.credits, e.enrollment_date FROM Enrollment e JOIN Course c ON e.course_id = c.course_id WHERE e.student_id = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new CourseResult(rs.getString("course_name"), rs.getInt("credits"), rs.getTimestamp("enrollment_date")));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding courses for student {}", studentId, e);
        }
        return results;
    }

    public List<StudentResult> findStudentsByCourseId(long courseId) {
        List<StudentResult> results = new ArrayList<>();
        String sql = "SELECT s.first_name, s.last_name, s.email, e.enrollment_date FROM Enrollment e JOIN Student s ON e.student_id = s.student_id WHERE e.course_id = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                    results.add(new StudentResult(fullName, rs.getString("email"), rs.getTimestamp("enrollment_date")));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding students for course {}", courseId, e);
        }
        return results;
    }

    public List<CourseResult> findTop10PopularCourses() {
        List<CourseResult> results = new ArrayList<>();
        String sql = "SELECT c.course_name, c.credits, COUNT(e.student_id) AS enrollment_count FROM Enrollment e JOIN Course c ON e.course_id = c.course_id GROUP BY c.course_id, c.course_name, c.credits ORDER BY enrollment_count DESC LIMIT 10";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(new CourseResult(rs.getString("course_name"), rs.getInt("credits"), rs.getLong("enrollment_count")));
            }
        } catch (SQLException e) {
            logger.error("Error finding top 10 popular courses", e);
        }
        return results;
    }

    public List<Student> findAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT student_id, first_name, last_name, date_of_birth, email FROM Student";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                students.add(new Student(rs.getLong("student_id"), rs.getString("first_name"), rs.getString("last_name"), rs.getDate("date_of_birth"), rs.getString("email")));
            }
        } catch (SQLException e) {
            logger.error("Error finding all students", e);
        }
        return students;
    }

    public List<Course> findAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, course_name, course_description, credits, teacher_id FROM Course";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                courses.add(new Course(rs.getLong("course_id"), rs.getString("course_name"), rs.getString("course_description"), rs.getInt("credits"), rs.getLong("teacher_id")));
            }
        } catch (SQLException e) {
            logger.error("Error finding all courses", e);
        }
        return courses;
    }

    public List<Enrollment> findAllEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT student_id, course_id, enrollment_date FROM Enrollment";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                enrollments.add(new Enrollment(rs.getLong("student_id"), rs.getLong("course_id"), rs.getTimestamp("enrollment_date")));
            }
        } catch (SQLException e) {
            logger.error("Error finding all enrollments", e);
        }
        return enrollments;
    }
}
