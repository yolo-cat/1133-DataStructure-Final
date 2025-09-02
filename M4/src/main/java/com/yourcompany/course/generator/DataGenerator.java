package com.yourcompany.course.generator;

import com.github.javafaker.Faker;
import com.yourcompany.course.model.Course;
import com.yourcompany.course.model.Enrollment;
import com.yourcompany.course.model.Student;
import com.yourcompany.course.model.Teacher;
import com.yourcompany.course.repository.DataRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * FINAL REFACTORED VERSION - Matches the correct database schema.
 */
public class DataGenerator {

    private static final int TEACHER_COUNT = 100;
    private static final int BATCH_SIZE = 5000;

    private final Faker faker = new Faker();
    private final Random random = new Random();
    private final DataRepository repository;

    public DataGenerator(DataRepository repository) {
        this.repository = repository;
    }

    public void generate(int scale, Consumer<String> progressConsumer) {
        try {
            progressConsumer.accept("--- Starting Data Generation Process ---");

            progressConsumer.accept("Step 1: Setting up database schema...");
            repository.setupDatabase();
            progressConsumer.accept("Database schema is ready.");

            progressConsumer.accept("Step 2: Generating and inserting teachers...");
            List<Teacher> teachers = generateAndInsertTeachers(progressConsumer);

            progressConsumer.accept("Step 3: Generating and inserting students...");
            List<Student> students = generateAndInsertStudents(scale, progressConsumer);

            progressConsumer.accept("Step 4: Generating and inserting courses...");
            List<Course> courses = generateAndInsertCourses(scale / 100, teachers, progressConsumer);

            progressConsumer.accept("Step 5: Generating and inserting enrollments...");
            generateAndInsertEnrollments(scale * 5, students, courses, progressConsumer);

            progressConsumer.accept("--- Data Generation Process Completed Successfully! ---");
        } catch (Exception e) {
            progressConsumer.accept("ERROR: Data generation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Teacher> generateAndInsertTeachers(Consumer<String> progressConsumer) {
        progressConsumer.accept(String.format("Generating and inserting %d teachers in batches of %d...", TEACHER_COUNT, BATCH_SIZE));
        List<Teacher> allTeachers = new ArrayList<>(TEACHER_COUNT);
        List<Teacher> batch = new ArrayList<>(BATCH_SIZE);
        Set<String> uniqueEmails = new HashSet<>(TEACHER_COUNT);

        while (allTeachers.size() + batch.size() < TEACHER_COUNT) {
            String email = faker.internet().emailAddress();
            if (uniqueEmails.add(email)) {
                batch.add(new Teacher(null, faker.name().fullName(), email));
                if (batch.size() == BATCH_SIZE) {
                    repository.batchInsertTeachers(batch);
                    allTeachers.addAll(batch);
                    progressConsumer.accept(String.format("Inserted batch. Total teachers so far: %d/%d", allTeachers.size(), TEACHER_COUNT));
                    batch.clear();
                }
            }
        }
        if (!batch.isEmpty()) {
            repository.batchInsertTeachers(batch);
            allTeachers.addAll(batch);
            progressConsumer.accept(String.format("Inserted batch. Total teachers so far: %d/%d", allTeachers.size(), TEACHER_COUNT));
            batch.clear();
        }
        return allTeachers;
    }

    private List<Student> generateAndInsertStudents(int studentCount, Consumer<String> progressConsumer) {
        progressConsumer.accept(String.format("Generating and inserting %d students in batches of %d...", studentCount, BATCH_SIZE));
        List<Student> allStudents = new ArrayList<>(studentCount);
        List<Student> batch = new ArrayList<>(BATCH_SIZE);
        Set<String> uniqueEmails = new HashSet<>(studentCount);

        while (allStudents.size() + batch.size() < studentCount) {
            String email = faker.internet().emailAddress();
            if (uniqueEmails.add(email)) {
                batch.add(new Student(null, faker.name().firstName(), faker.name().lastName(), faker.date().birthday(18, 25), email));
                if (batch.size() == BATCH_SIZE) {
                    repository.batchInsertStudents(batch);
                    allStudents.addAll(batch);
                    progressConsumer.accept(String.format("Inserted batch. Total students so far: %d/%d", allStudents.size(), studentCount));
                    batch.clear();
                }
            }
        }
        if (!batch.isEmpty()) {
            repository.batchInsertStudents(batch);
            allStudents.addAll(batch);
            progressConsumer.accept(String.format("Inserted batch. Total students so far: %d/%d", allStudents.size(), studentCount));
            batch.clear();
        }
        return allStudents;
    }

    private List<Course> generateAndInsertCourses(int courseCount, List<Teacher> teachers, Consumer<String> progressConsumer) {
        progressConsumer.accept(String.format("Generating and inserting %d courses in batches of %d...", courseCount, BATCH_SIZE));
        List<Course> allCourses = new ArrayList<>(courseCount);
        List<Course> batch = new ArrayList<>(BATCH_SIZE);

        for (int i = 0; i < courseCount; i++) {
            Teacher teacher = teachers.get(random.nextInt(teachers.size()));
            batch.add(new Course(null, faker.educator().course(), faker.lorem().sentence(), faker.number().numberBetween(1, 5), teacher.getTeacherId()));
            if (batch.size() == BATCH_SIZE || i == courseCount - 1) {
                repository.batchInsertCourses(batch);
                allCourses.addAll(batch);
                progressConsumer.accept(String.format("Inserted batch. Total courses so far: %d/%d", allCourses.size(), courseCount));
                batch.clear();
            }
        }
        return allCourses;
    }

    private void generateAndInsertEnrollments(int enrollmentCount, List<Student> students, List<Course> courses, Consumer<String> progressConsumer) {
        progressConsumer.accept(String.format("Generating and inserting %d enrollments in batches of %d...", enrollmentCount, BATCH_SIZE));
        
        List<Enrollment> batch = new ArrayList<>(BATCH_SIZE);
        int totalGenerated = 0;

        // Systematic generation to avoid performance issues with random collisions.
        // This guarantees unique pairs and is significantly faster.
        outer_loop:
        for (int i = 0; i < students.size(); i++) {
            for (int j = 0; j < courses.size(); j++) {
                if (totalGenerated >= enrollmentCount) {
                    break outer_loop;
                }

                // A simple mixing strategy to make the data distribution less linear
                // without the performance cost of full randomness.
                Student student = students.get(i);
                Course course = courses.get((i + j) % courses.size());

                batch.add(new Enrollment(student.getStudentId(), course.getCourseId(), faker.date().past(365 * 2, TimeUnit.DAYS)));
                totalGenerated++;

                if (batch.size() == BATCH_SIZE) {
                    repository.batchInsertEnrollments(batch);
                    progressConsumer.accept(String.format("Inserted batch. Total enrollments so far: %d/%d", totalGenerated, enrollmentCount));
                    batch.clear();
                }
            }
        }

        // Insert any remaining enrollments in the last batch.
        if (!batch.isEmpty()) {
            repository.batchInsertEnrollments(batch);
            progressConsumer.accept(String.format("Inserted batch. Total enrollments so far: %d/%d", totalGenerated, enrollmentCount));
        }
    }
}
