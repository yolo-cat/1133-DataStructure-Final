package com.yourcompany.course.search;

import com.yourcompany.course.model.dto.CourseResult;
import com.yourcompany.course.model.dto.StudentResult;

import java.util.Collections;
import java.util.List;

/**
 * 方案B: 記憶體驅動的搜尋服務實現 (Placeholder)。
 */
public class InMemorySearchService implements SearchService {

    private boolean isDataLoaded = false;

    /**
     * 將資料從資料庫載入到記憶體中。
     * 這是一個耗時操作。
     */
    public void loadData() {
        // Placeholder: 實際應從資料庫讀取資料並建立 HashMap 索引
        System.out.println("In-Memory Service: Loading data into memory...");
        try {
            // 模擬耗時操作
            Thread.sleep(2000); // 模擬2秒載入時間
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        isDataLoaded = true;
        System.out.println("In-Memory Service: Data loaded.");
    }

    @Override
    public List<CourseResult> findCoursesByStudent(long studentId) {
        // Placeholder
        if (!isDataLoaded) {
            System.out.println("In-Memory Service: Data not loaded!");
            return Collections.emptyList();
        }
        System.out.println("In-Memory Service: Finding courses for student " + studentId);
        return Collections.emptyList();
    }

    @Override
    public List<StudentResult> findStudentsByCourse(long courseId) {
        // Placeholder
        if (!isDataLoaded) {
            System.out.println("In-Memory Service: Data not loaded!");
            return Collections.emptyList();
        }
        System.out.println("In-Memory Service: Finding students for course " + courseId);
        return Collections.emptyList();
    }

    @Override
    public List<CourseResult> findTop10PopularCourses() {
        // Placeholder
        if (!isDataLoaded) {
            System.out.println("In-Memory Service: Data not loaded!");
            return Collections.emptyList();
        }
        System.out.println("In-Memory Service: Finding top 10 popular courses");
        return Collections.emptyList();
    }

    @Override
    public String getServiceName() {
        return "In-Memory Search Service";
    }
}
