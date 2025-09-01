package com.yourcompany.course.repository;

/**
 * 資料庫存取層 (Placeholder)。
 * 負責所有底層的 JDBC 操作，如批次插入、查詢等。
 */
public class DataRepository {

    /**
     * 清空所有相關表格的資料。
     */
    public void clearDatabase() {
        // Placeholder: 實際應執行 DELETE FROM ... SQL 命令
        System.out.println("Repository: Clearing all tables in the database...");
        try {
            Thread.sleep(500); // 模擬 I/O 操作
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Repository: Database cleared.");
    }

    /**
     * 批次插入學生資料。
     * @param count 要插入的數量
     */
    public void batchInsertStudents(int count) {
        // Placeholder
        System.out.printf("Repository: Batch inserting %d students...\n", count);
    }

    /**
     * 批次插入課程資料。
     * @param count 要插入的數量
     */
    public void batchInsertCourses(int count) {
        // Placeholder
        System.out.printf("Repository: Batch inserting %d courses...\n", count);
    }

    /**
     * 批次插入選課紀錄。
     * @param count 要插入的數量
     */
    public void batchInsertEnrollments(int count) {
        // Placeholder
        System.out.printf("Repository: Batch inserting %d enrollments...\n", count);
    }
}
