package com.yourcompany.course.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 負責資料庫連線池的配置與初始化。
 * 使用 HikariCP 提供高效能的連線池。
 */
public class DatabaseConfig {

    private static final String PROPS_FILE = "/database.properties";
    private static HikariDataSource dataSource;

    /**
     * 獲取設定好的 DataSource 單例。
     * @return DataSource 實例
     */
    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            initDataSource();
        }
        return dataSource;
    }

    private static void initDataSource() {
        try {
            Properties props = loadProperties();

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("jdbc.url"));
            config.setUsername(props.getProperty("jdbc.user"));
            config.setPassword(props.getProperty("jdbc.password"));
            config.setDriverClassName(props.getProperty("spring.datasource.driver-class-name"));
            
            // --- HikariCP 效能調優 ---
            config.setMaximumPoolSize(20); // 最大連線數
            config.setMinimumIdle(5); // 最小閒置連線數
            config.setConnectionTimeout(30000); // 連線逾時時間
            config.setIdleTimeout(600000); // 閒置連線存活時間
            config.setMaxLifetime(1800000); // 連線最大存活時間
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);

        } catch (IOException e) {
            // In a real app, use a logger
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database configuration.", e);
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = DatabaseConfig.class.getResourceAsStream(PROPS_FILE)) {
            if (inputStream == null) {
                throw new IOException("Database properties file not found: " + PROPS_FILE);
            }
            properties.load(inputStream);
        }
        return properties;
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
