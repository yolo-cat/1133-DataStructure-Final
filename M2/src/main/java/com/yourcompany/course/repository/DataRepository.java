package com.yourcompany.course.repository;

import com.yourcompany.course.model.dto.CourseResult;
import com.yourcompany.course.model.dto.StudentResult;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 負責所有底層資料庫操作 (JDBC)。
 * 這個類別封裝了所有 SQL 查詢，並提供給服務層 (SearchService) 調用。
 */
public class DataRepository {

    private final DataSource dataSource;

    public DataRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public List<CourseResult> findCoursesByStudentId(long studentId) {
        List<CourseResult> results = new ArrayList<>();
        String sql = "SELECT c.course_name, c.credits, e.enrollment_date " +
                     "FROM enrollments e " +
                     "JOIN courses c ON e.course_id = c.id " +
                     "WHERE e.student_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CourseResult course = new CourseResult();
                    course.setCourseName(rs.getString("course_name"));
                    course.setCredits(rs.getInt("credits"));
                    course.setEnrollmentDate(rs.getDate("enrollment_date"));
                    results.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<StudentResult> findStudentsByCourseId(long courseId) {
        List<StudentResult> results = new ArrayList<>();
        String sql = "SELECT s.student_name, s.email, e.enrollment_date " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.student_id = s.id " +
                     "WHERE e.course_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentResult student = new StudentResult();
                    student.setStudentName(rs.getString("student_name"));
                    student.setEmail(rs.getString("email"));
                    student.setEnrollmentDate(rs.getDate("enrollment_date"));
                    results.add(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * 功能 4: 查找選修人數最多的前10門熱門課程。
     * @return 熱門課程結果列表
     */
    public List<CourseResult> findTop10PopularCourses() {
        List<CourseResult> results = new ArrayList<>();
        String sql = "SELECT c.course_name, c.credits, COUNT(e.student_id) AS enrollment_count " +
                     "FROM enrollments e " +
                     "JOIN courses c ON e.course_id = c.id " +
                     "GROUP BY c.id, c.course_name, c.credits " +
                     "ORDER BY enrollment_count DESC " +
                     "LIMIT 10";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CourseResult course = new CourseResult();
                course.setCourseName(rs.getString("course_name"));
                course.setCredits(rs.getInt("credits"));
                course.setEnrollmentCount(rs.getLong("enrollment_count"));
                results.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
