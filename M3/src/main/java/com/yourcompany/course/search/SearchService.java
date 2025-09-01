package com.yourcompany.course.search;

import com.yourcompany.course.model.dto.CourseResult;
import com.yourcompany.course.model.dto.StudentResult;
import java.util.List;

/**
 * 定義所有搜尋功能的標準介面。
 * 專案中的兩種搜尋方案 (SQL驅動和記憶體驅動) 都必須實現此介面。
 */
public interface SearchService {

    /**
     * 功能 1: 根據學生ID查找其選修的所有課程。
     *
     * @param studentId 學生ID
     * @return 包含課程資訊的列表，若無結果則返回空列表。
     */
    List<CourseResult> findCoursesByStudent(long studentId);

    /**
     * 功能 2 & 3: 根據課程ID查找所有選修該課程的學生。
     *
     * @param courseId 課程ID
     * @return 包含學生資訊的列表，若無結果則返回空列表。
     */
    List<StudentResult> findStudentsByCourse(long courseId);

    /**
     * 功能 4: 查找選修人數最多的前10門熱門課程。
     *
     * @return 包含熱門課程資訊的列表，按選修人數降序排列。
     */
    List<CourseResult> findTop10PopularCourses();

    /**
     * (可選) 獲取服務名稱，用於在UI或日誌中標識。
     * @return "SQL Search Service" 或 "In-Memory Search Service"
     */
    default String getServiceName() {
        return this.getClass().getSimpleName();
    }
}
