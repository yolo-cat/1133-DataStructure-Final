DROP TABLE IF EXISTS Enrollment;
DROP TABLE IF EXISTS Course;
DROP TABLE IF EXISTS Student;
DROP TABLE IF EXISTS Teacher;

CREATE TABLE Teacher (
    teacher_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE Student (
    student_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE Course (
    course_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(255) NOT NULL,
    course_description TEXT,
    credits INT NOT NULL,
    teacher_id BIGINT,
    FOREIGN KEY (teacher_id) REFERENCES Teacher(teacher_id)
);

CREATE TABLE Enrollment (
    student_id BIGINT,
    course_id BIGINT,
    enrollment_date TIMESTAMP NOT NULL,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES Student(student_id),
    FOREIGN KEY (course_id) REFERENCES Course(course_id)
);

CREATE INDEX idx_enrollment_student_id ON Enrollment(student_id);
CREATE INDEX idx_enrollment_course_id ON Enrollment(course_id);
