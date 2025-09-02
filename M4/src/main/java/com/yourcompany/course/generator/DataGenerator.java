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
 * FINAL REFACTORED VERSION - Smart generator that checks existing data.
 */
public class DataGenerator {

    private static final int BATCH_SIZE = 5000;

    private final Faker faker = new Faker();
    private final Random random = new Random();
    private final DataRepository repository;

    public DataGenerator(DataRepository repository) {
        this.repository = repository;
    }

    public void generate(int studentCount, int courseCount, int teacherCount, int enrollmentCount, Consumer<String> progressConsumer) {
        try {
            progressConsumer.accept("--- Starting Smart Data Generation Process ---");

            progressConsumer.accept("\nStep 1: Setting up database schema...");
            repository.setupDatabase();
            progressConsumer.accept("Database schema is ready.");

            List<Teacher> teachers = handleTeachersGeneration(teacherCount, progressConsumer);
            List<Student> students = handleStudentsGeneration(studentCount, progressConsumer);
            List<Course> courses = handleCoursesGeneration(courseCount, teachers, progressConsumer);
            handleEnrollmentsGeneration(enrollmentCount, students, courses, progressConsumer);

            progressConsumer.accept("\n--- Data Generation Process Completed Successfully! ---");
        } catch (Exception e) {
            progressConsumer.accept("ERROR: Data generation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Teacher> handleTeachersGeneration(int requiredCount, Consumer<String> progressConsumer) {
        progressConsumer.accept(String.format("\nStep 2: Checking Teacher data (target: %d)...", requiredCount));
        long currentCount = repository.countTeachers();
        if (currentCount >= requiredCount) {
            progressConsumer.accept(String.format("OK. Found %d teachers, skipping generation.", currentCount));
        } else {
            long needed = requiredCount - currentCount;
            progressConsumer.accept(String.format("Found %d teachers, generating %d more...", currentCount, needed));
            List<Teacher> newTeachers = new ArrayList<>();
            Set<String> uniqueEmails = new HashSet<>();
            while (newTeachers.size() < needed) {
                String email = faker.internet().emailAddress();
                if (uniqueEmails.add(email)) {
                    newTeachers.add(new Teacher(null, faker.name().fullName(), email));
                }
            }
            repository.batchInsertTeachers(newTeachers);
        }
        return repository.findAllTeachers();
    }

    private List<Student> handleStudentsGeneration(int requiredCount, Consumer<String> progressConsumer) {
        progressConsumer.accept(String.format("\nStep 3: Checking Student data (target: %d)...", requiredCount));
        long currentCount = repository.countStudents();
        if (currentCount >= requiredCount) {
            progressConsumer.accept(String.format("OK. Found %d students, skipping generation.", currentCount));
        } else {
            long needed = requiredCount - currentCount;
            progressConsumer.accept(String.format("Found %d students, generating %d more...", currentCount, needed));
            List<Student> newStudents = new ArrayList<>();
            Set<String> uniqueEmails = new HashSet<>();
            while (newStudents.size() < needed) {
                String email = faker.internet().emailAddress();
                if (uniqueEmails.add(email)) {
                    newStudents.add(new Student(null, faker.name().firstName(), faker.name().lastName(), faker.date().birthday(18, 25), email));
                }
            }
            repository.batchInsertStudents(newStudents);
        }
        return repository.findAllStudents();
    }

    private List<Course> handleCoursesGeneration(int requiredCount, List<Teacher> teachers, Consumer<String> progressConsumer) {
        progressConsumer.accept(String.format("\nStep 4: Checking Course data (target: %d)...", requiredCount));
        if (teachers.isEmpty()) {
            progressConsumer.accept("ERROR: Cannot generate courses without teachers.");
            return new ArrayList<>();
        }
        long currentCount = repository.countCourses();
        if (currentCount >= requiredCount) {
            progressConsumer.accept(String.format("OK. Found %d courses, skipping generation.", currentCount));
        } else {
            long needed = requiredCount - currentCount;
            progressConsumer.accept(String.format("Found %d courses, generating %d more...", currentCount, needed));
            List<Course> newCourses = new ArrayList<>();
            for (int i = 0; i < needed; i++) {
                Teacher teacher = teachers.get(random.nextInt(teachers.size()));
                newCourses.add(new Course(null, faker.educator().course(), faker.lorem().sentence(), faker.number().numberBetween(1, 5), teacher.getTeacherId()));
            }
            repository.batchInsertCourses(newCourses);
        }
        return repository.findAllCourses();
    }

    private void handleEnrollmentsGeneration(int requiredCount, List<Student> students, List<Course> courses, Consumer<String> progressConsumer) {
        progressConsumer.accept(String.format("\nStep 5: Checking Enrollment data (target: %d)...", requiredCount));
        if (students.isEmpty() || courses.isEmpty()) {
            progressConsumer.accept("ERROR: Cannot generate enrollments without students and courses.");
            return;
        }
        long currentCount = repository.countEnrollments();
        if (currentCount == requiredCount) {
            progressConsumer.accept(String.format("OK. Found %d enrollments, which matches the target. Skipping generation.", currentCount));
            return;
        }

        progressConsumer.accept(String.format("Found %d enrollments. Clearing and regenerating to meet target of %d...", currentCount, requiredCount));
        repository.truncateEnrollments();

        List<Enrollment> batch = new ArrayList<>(BATCH_SIZE);
        int totalGenerated = 0;

        outer_loop:
        for (int i = 0; i < students.size(); i++) {
            for (int j = 0; j < courses.size(); j++) {
                if (totalGenerated >= requiredCount) {
                    break outer_loop;
                }
                Student student = students.get(i);
                Course course = courses.get((i + j) % courses.size());
                batch.add(new Enrollment(student.getStudentId(), course.getCourseId(), faker.date().past(365 * 2, TimeUnit.DAYS)));
                totalGenerated++;

                if (batch.size() == BATCH_SIZE) {
                    repository.batchInsertEnrollments(batch);
                    progressConsumer.accept(String.format("Inserted batch. Total enrollments so far: %d/%d", totalGenerated, requiredCount));
                    batch.clear();
                }
            }
        }

        if (!batch.isEmpty()) {
            repository.batchInsertEnrollments(batch);
            progressConsumer.accept(String.format("Inserted batch. Total enrollments so far: %d/%d", totalGenerated, requiredCount));
        }
    }
}
