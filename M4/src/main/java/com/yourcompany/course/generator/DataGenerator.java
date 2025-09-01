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
        List<Teacher> teachers = new ArrayList<>(TEACHER_COUNT);
        for (int i = 0; i < TEACHER_COUNT; i++) {
            teachers.add(new Teacher(null, faker.name().fullName(), faker.internet().emailAddress()));
        }
        repository.batchInsertTeachers(teachers);
        progressConsumer.accept(String.format("Generated and inserted %d teachers.", teachers.size()));
        return teachers;
    }

    private List<Student> generateAndInsertStudents(int studentCount, Consumer<String> progressConsumer) {
        List<Student> students = new ArrayList<>(studentCount);
        for (int i = 0; i < studentCount; i++) {
            students.add(new Student(null, faker.name().firstName(), faker.name().lastName(), faker.date().birthday(18, 25), faker.internet().emailAddress()));
        }
        repository.batchInsertStudents(students);
        progressConsumer.accept(String.format("Generated and inserted %d students.", students.size()));
        return students;
    }

    private List<Course> generateAndInsertCourses(int courseCount, List<Teacher> teachers, Consumer<String> progressConsumer) {
        List<Course> courses = new ArrayList<>(courseCount);
        for (int i = 0; i < courseCount; i++) {
            Teacher teacher = teachers.get(random.nextInt(teachers.size()));
            courses.add(new Course(null, faker.educator().course(), faker.lorem().sentence(), faker.number().numberBetween(1, 5), teacher.getTeacherId()));
        }
        repository.batchInsertCourses(courses);
        progressConsumer.accept(String.format("Generated and inserted %d courses.", courses.size()));
        return courses;
    }

    private void generateAndInsertEnrollments(int enrollmentCount, List<Student> students, List<Course> courses, Consumer<String> progressConsumer) {
        progressConsumer.accept(String.format("Generating and inserting %d enrollments in batches of %d...", enrollmentCount, BATCH_SIZE));
        int totalGenerated = 0;
        Set<String> uniqueEnrollments = new HashSet<>(enrollmentCount);

        while (totalGenerated < enrollmentCount) {
            List<Enrollment> batch = new ArrayList<>(BATCH_SIZE);
            int batchTargetSize = Math.min(BATCH_SIZE, enrollmentCount - totalGenerated);

            while (batch.size() < batchTargetSize) {
                Student student = students.get(random.nextInt(students.size()));
                Course course = courses.get(random.nextInt(courses.size()));
                String key = student.getStudentId() + "-" + course.getCourseId();

                if (uniqueEnrollments.add(key)) {
                    batch.add(new Enrollment(null, student.getStudentId(), course.getCourseId(), faker.date().past(365 * 2, TimeUnit.DAYS)));
                }
            }

            if (!batch.isEmpty()) {
                repository.batchInsertEnrollments(batch);
                totalGenerated += batch.size();
                progressConsumer.accept(String.format("Inserted batch. Total enrollments so far: %d/%d", totalGenerated, enrollmentCount));
            }
        }
    }
}
