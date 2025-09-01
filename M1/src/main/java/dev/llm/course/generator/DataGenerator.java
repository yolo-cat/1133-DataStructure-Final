package dev.llm.course.generator;

import com.github.javafaker.Faker;
import dev.llm.course.model.Course;
import dev.llm.course.model.Enrollment;
import dev.llm.course.model.Student;
import dev.llm.course.model.Teacher;
import dev.llm.course.repository.DataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DataGenerator {

    private static final Logger logger = LoggerFactory.getLogger(DataGenerator.class);

    private static final int TEACHER_COUNT = 100;
    private static final int STUDENT_COUNT = 10000;
    private static final int COURSE_COUNT = 1000;
    private static final int ENROLLMENT_COUNT = 1000000;
    private static final int BATCH_SIZE = 5000;

    private final Faker faker = new Faker();
    private final Random random = new Random();
    private final DataRepository repository;

    public DataGenerator(DataRepository repository) {
        this.repository = repository;
    }

    public void generateAndInsertData() {
        logger.info("Starting data generation process...");

        repository.setupDatabase();

        List<Teacher> teachers = generateTeachers();
        repository.batchInsertTeachers(teachers);

        List<Student> students = generateStudents();
        repository.batchInsertStudents(students);

        List<Course> courses = generateCourses(teachers);
        repository.batchInsertCourses(courses);

        generateAndInsertEnrollmentsInBatches(students, courses);

        logger.info("Data generation process completed successfully.");
    }

    private List<Teacher> generateTeachers() {
        List<Teacher> teachers = new ArrayList<>(TEACHER_COUNT);
        for (int i = 0; i < TEACHER_COUNT; i++) {
            Teacher teacher = new Teacher();
            teacher.setFullName(faker.name().fullName());
            teacher.setEmail(faker.internet().emailAddress());
            teachers.add(teacher);
        }
        logger.info("Generated {} teachers.", teachers.size());
        return teachers;
    }

    private List<Student> generateStudents() {
        List<Student> students = new ArrayList<>(STUDENT_COUNT);
        for (int i = 0; i < STUDENT_COUNT; i++) {
            Student student = new Student();
            student.setFullName(faker.name().fullName());
            student.setEmail(faker.internet().emailAddress());
            student.setBirthDate(faker.date().birthday(18, 25));
            students.add(student);
        }
        logger.info("Generated {} students.", students.size());
        return students;
    }

    private List<Course> generateCourses(List<Teacher> teachers) {
        List<Course> courses = new ArrayList<>(COURSE_COUNT);
        for (int i = 0; i < COURSE_COUNT; i++) {
            Course course = new Course();
            course.setCourseName(faker.educator().course());
            course.setCredits(faker.number().numberBetween(1, 5));
            course.setTeacherId(teachers.get(random.nextInt(teachers.size())).getId());
            courses.add(course);
        }
        logger.info("Generated {} courses.", courses.size());
        return courses;
    }

    private void generateAndInsertEnrollmentsInBatches(List<Student> students, List<Course> courses) {
        logger.info("Generating and inserting {} enrollments in batches of {}...", ENROLLMENT_COUNT, BATCH_SIZE);
        int totalGenerated = 0;
        Set<String> uniqueEnrollments = new HashSet<>(ENROLLMENT_COUNT);

        while (totalGenerated < ENROLLMENT_COUNT) {
            List<Enrollment> batch = new ArrayList<>(BATCH_SIZE);
            int batchTargetSize = Math.min(BATCH_SIZE, ENROLLMENT_COUNT - totalGenerated);

            while (batch.size() < batchTargetSize) {
                Student student = students.get(random.nextInt(students.size()));
                Course course = courses.get(random.nextInt(courses.size()));
                String key = student.getId() + "-" + course.getId();

                if (uniqueEnrollments.add(key)) {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setStudentId(student.getId());
                    enrollment.setCourseId(course.getId());
                    enrollment.setEnrollmentDate(faker.date().past(365 * 2, TimeUnit.DAYS));
                    batch.add(enrollment);
                }
            }

            if (!batch.isEmpty()) {
                repository.batchInsertEnrollments(batch);
                totalGenerated += batch.size();
                logger.info("Inserted batch. Total enrollments so far: {}/{}", totalGenerated, ENROLLMENT_COUNT);
            }
        }
    }
}
