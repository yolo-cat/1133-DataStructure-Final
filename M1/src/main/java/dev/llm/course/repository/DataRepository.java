package dev.llm.course.repository;

import dev.llm.course.config.DatabaseConfig;
import dev.llm.course.model.Course;
import dev.llm.course.model.Enrollment;
import dev.llm.course.model.Student;
import dev.llm.course.model.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

public class DataRepository {

    private static final Logger logger = LoggerFactory.getLogger(DataRepository.class);

    public void setupDatabase() {
        String schema = "";
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db_schema.sql")) {
            if (in == null) {
                throw new RuntimeException("db_schema.sql not found");
            }
            schema = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            logger.error("Failed to read db_schema.sql", e);
            throw new RuntimeException(e);
        }

        try (Connection conn = DatabaseConfig.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(schema);
            logger.info("Database schema created successfully.");
        } catch (SQLException e) {
            logger.error("Failed to execute schema script.", e);
            throw new RuntimeException(e);
        }
    }

    public void batchInsertTeachers(List<Teacher> teachers) {
        String sql = "INSERT INTO teachers (full_name, email) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (Teacher teacher : teachers) {
                pstmt.setString(1, teacher.getFullName());
                pstmt.setString(2, teacher.getEmail());
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            int i = 0;
            while (generatedKeys.next()) {
                teachers.get(i++).setId(generatedKeys.getLong(1));
            }
            logger.info("Successfully batch inserted {} teachers.", teachers.size());
        } catch (SQLException e) {
            logger.error("Batch insert for teachers failed.", e);
        }
    }

    public void batchInsertStudents(List<Student> students) {
        String sql = "INSERT INTO students (full_name, birth_date, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (Student student : students) {
                pstmt.setString(1, student.getFullName());
                pstmt.setDate(2, new java.sql.Date(student.getBirthDate().getTime()));
                pstmt.setString(3, student.getEmail());
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            int i = 0;
            while (generatedKeys.next()) {
                students.get(i++).setId(generatedKeys.getLong(1));
            }
            logger.info("Successfully batch inserted {} students.", students.size());
        } catch (SQLException e) {
            logger.error("Batch insert for students failed.", e);
        }
    }

    public void batchInsertCourses(List<Course> courses) {
        String sql = "INSERT INTO courses (course_name, credits, teacher_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (Course course : courses) {
                pstmt.setString(1, course.getCourseName());
                pstmt.setInt(2, course.getCredits());
                pstmt.setLong(3, course.getTeacherId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            int i = 0;
            while (generatedKeys.next()) {
                courses.get(i++).setId(generatedKeys.getLong(1));
            }
            logger.info("Successfully batch inserted {} courses.", courses.size());
        } catch (SQLException e) {
            logger.error("Batch insert for courses failed.", e);
        }
    }

    public void batchInsertEnrollments(List<Enrollment> enrollments) {
        String sql = "INSERT INTO enrollments (student_id, course_id, enrollment_date) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Enrollment enrollment : enrollments) {
                pstmt.setLong(1, enrollment.getStudentId());
                pstmt.setLong(2, enrollment.getCourseId());
                pstmt.setTimestamp(3, new java.sql.Timestamp(enrollment.getEnrollmentDate().getTime()));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            logger.error("Batch insert for enrollments failed.", e);
        }
    }
}
