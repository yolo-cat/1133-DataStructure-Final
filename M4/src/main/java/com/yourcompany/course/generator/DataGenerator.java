package com.yourcompany.course.generator;

import com.yourcompany.course.repository.DataRepository;

import java.util.function.Consumer;

/**
 * 負責生成大量假資料並存入資料庫 (Placeholder)。
 */
public class DataGenerator {

    private final DataRepository repository;

    public DataGenerator(DataRepository repository) {
        this.repository = repository;
    }

    /**
     * 生成並插入指定規模的資料。
     *
     * @param scale 資料規模 (e.g., 10000, 100000, 1000000)
     * @param progressConsumer 用於回報進度的消費者，可以將進度訊息更新到 GUI。
     */
    public void generate(int scale, Consumer<String> progressConsumer) {
        progressConsumer.accept("開始生成資料，目標規模: " + scale);

        try {
            progressConsumer.accept("正在清空舊資料...");
            repository.clearDatabase();
            progressConsumer.accept("舊資料已清空。");

            int studentCount = scale;
            int courseCount = Math.max(1, scale / 100); // 確保至少有1門課程
            int enrollmentCount = scale * 5; // 假設平均每個學生選5門課

            Thread.sleep(200); // 模擬延遲
            progressConsumer.accept(String.format("準備插入 %d 筆學生資料...", studentCount));
            repository.batchInsertStudents(studentCount);
            Thread.sleep(1000); // 模擬耗時

            progressConsumer.accept(String.format("準備插入 %d 筆課程資料...", courseCount));
            repository.batchInsertCourses(courseCount);
            Thread.sleep(500); // 模擬耗時

            progressConsumer.accept(String.format("準備插入 %d 筆選課紀錄...", enrollmentCount));
            repository.batchInsertEnrollments(enrollmentCount);
            Thread.sleep(2000); // 模擬耗時

            progressConsumer.accept("資料生成完畢！");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            progressConsumer.accept("資料生成被中斷。");
        }
    }
}
