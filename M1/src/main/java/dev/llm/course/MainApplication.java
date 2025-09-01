package dev.llm.course;

import dev.llm.course.config.DatabaseConfig;
import dev.llm.course.generator.DataGenerator;
import dev.llm.course.repository.DataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApplication {

    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    public static void main(String[] args) {
        logger.info("Application starting...");

        try {
            DataRepository dataRepository = new DataRepository();
            DataGenerator dataGenerator = new DataGenerator(dataRepository);

            long startTime = System.currentTimeMillis();
            dataGenerator.generateAndInsertData();
            long endTime = System.currentTimeMillis();

            logger.info("Total data generation time: {} ms", (endTime - startTime));

        } catch (Exception e) {
            logger.error("An error occurred during the data generation process.", e);
        } finally {
            DatabaseConfig.close();
            logger.info("Database connection pool closed. Application finished.");
        }
    }
}
