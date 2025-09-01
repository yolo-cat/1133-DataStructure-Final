package com.yourcompany.course.model.dto;

/**
 * 用於封裝效能測試結果的資料傳輸物件 (DTO)。
 */
public class BenchmarkResult {
    private final long totalTimeNanos;    // 總耗時 (奈秒)
    private final double averageTimeNanos;  // 平均耗時 (奈秒)
    private final int iterations;         // 執行次數

    public BenchmarkResult(long totalTimeNanos, int iterations) {
        this.totalTimeNanos = totalTimeNanos;
        this.iterations = iterations;
        if (iterations > 0) {
            this.averageTimeNanos = (double) totalTimeNanos / iterations;
        } else {
            this.averageTimeNanos = 0;
        }
    }

    public long getTotalTimeNanos() {
        return totalTimeNanos;
    }

    public double getAverageTimeNanos() {
        return averageTimeNanos;
    }

    public int getIterations() {
        return iterations;
    }

    @Override
    public String toString() {
        return String.format(
            "測試報告: 總共執行 %d 次, 總耗時: %.2f ms, 平均耗時: %.4f ms",
            iterations,
            totalTimeNanos / 1_000_000.0,
            averageTimeNanos / 1_000_000.0
        );
    }
}
