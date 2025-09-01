CREATE TABLE teachers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(150) NOT NULL,
    credits INT NOT NULL,
    teacher_id BIGINT,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id)
);

CREATE TABLE enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    course_id BIGINT,
    enrollment_date TIMESTAMP NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- 根據 DR-2 規範，為 student_id 和 course_id 建立獨立的 B-Tree 索引以優化查詢效能
CREATE INDEX idx_student_id ON enrollments(student_id);
CREATE INDEX idx_course_id ON enrollments(course_id);
