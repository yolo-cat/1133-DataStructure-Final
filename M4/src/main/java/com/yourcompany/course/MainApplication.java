package com.yourcompany.course;

import com.yourcompany.course.config.DatabaseConfig;
import com.yourcompany.course.generator.DataGenerator;
import com.yourcompany.course.repository.DataRepository;
import com.yourcompany.course.search.InMemorySearchService;
import com.yourcompany.course.search.SqlSearchService;
import com.yourcompany.course.ui.MainFrame;

import javax.sql.DataSource;
import javax.swing.*;

/**
 * 專案主入口 (Composition Root)。
 * 負責初始化所有服務、依賴注入以及啟動 GUI。
 */
public class MainApplication {

    public static void main(String[] args) {
        // 1. 初始化資料庫連線池
        DataSource dataSource = DatabaseConfig.getDataSource();

        // 2. 建立 Repository (資料存取層)
        DataRepository dataRepository = new DataRepository(dataSource);

        // 3. 建立 Generator 和 Services (業務邏輯層)
        DataGenerator dataGenerator = new DataGenerator(dataRepository);
        SqlSearchService sqlSearchService = new SqlSearchService(dataRepository);
        InMemorySearchService inMemorySearchService = new InMemorySearchService(dataRepository);

        // 4. 註冊關閉鉤子，確保程式結束時關閉連線池
        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseConfig::closeDataSource));

        // 5. 啟動 GUI 並注入所有依賴
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(sqlSearchService, inMemorySearchService, dataGenerator);
            mainFrame.setVisible(true);
        });
    }
}
