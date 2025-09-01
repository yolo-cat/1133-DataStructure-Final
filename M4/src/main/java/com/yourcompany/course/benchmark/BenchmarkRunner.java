package com.yourcompany.course.benchmark;

import com.yourcompany.course.model.dto.BenchmarkResult;
import com.yourcompany.course.search.SearchService;

import java.util.List;
import java.util.function.Consumer;

/**
 * 負責執行效能測試的自動化工具。
 */
public class BenchmarkRunner {

    private static final int DEFAULT_ITERATIONS = 1000;

    /**
     * 執行對 SearchService 的效能測試。
     *
     * @param service 要測試的 SearchService 實例。
     * @param testData 測試數據，例如一組隨機的 studentId。
     * @param searchOperation 要執行的搜尋操作，是一個 Consumer，接收 SearchService 和一個 long 型的 id。
     * @return 包含效能數據的結果物件。
     */
    public static BenchmarkResult run(SearchService service, List<Long> testData, Consumer<Long> searchOperation) {
        if (testData == null || testData.isEmpty()) {
            return new BenchmarkResult(0, 0);
        }

        int iterations = Math.min(DEFAULT_ITERATIONS, testData.size());

        // 預熱 (Warm-up) - 可選，但有助於 JIT 編譯後獲得更穩定的結果
        for (int i = 0; i < iterations / 10; i++) {
            searchOperation.accept(testData.get(i));
        }

        long startTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            searchOperation.accept(testData.get(i));
        }

        long endTime = System.nanoTime();

        long totalTime = endTime - startTime;
        return new BenchmarkResult(totalTime, iterations);
    }

    /**
     * 針對 findCoursesByStudent 方法的便捷測試方法。
     */
    public static BenchmarkResult runFindCoursesByStudent(SearchService service, List<Long> studentIds) {
        return run(service, studentIds, service::findCoursesByStudent);
    }

    /**
     * 針對 findStudentsByCourse 方法的便捷測試方法。
     */
    public static BenchmarkResult runFindStudentsByCourse(SearchService service, List<Long> courseIds) {
        return run(service, courseIds, service::findStudentsByCourse);
    }
}
