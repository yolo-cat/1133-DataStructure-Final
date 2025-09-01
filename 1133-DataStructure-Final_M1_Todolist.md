好的，遵照您的要求，這裡將前述的任務架構拆解成一份詳細、可執行的任務清單。這份清單按照開發的邏輯順序排列，您可以依此逐步完成整個專案。

---

### **巨量資料生成任務 - 執行步驟清單**

#### **階段 0: 專案初始化與環境設定 (Preparation & Setup)**

*   [ ] **1. 設定開發環境:**
    *   安裝 Java Development Kit (JDK 11 或更高版本)。
    *   安裝一個 IDE (整合開發環境)，例如 IntelliJ IDEA 或 Eclipse。
    *   安裝 Maven 或 Gradle 作為專案建構工具。

*   [ ] **2. 設定資料庫:**
    *   安裝並啟動資料庫伺服器 (例如 MySQL 或 PostgreSQL)。
    *   使用資料庫客戶端工具 (如 DBeaver, DataGrip) 建立一個新的資料庫 (例如 `school_data`)。

*   [ ] **3. 建立專案:**
    *   使用 Maven/Gradle 建立一個新的 Java 專案。

*   [ ] **4. 加入專案依賴 (Dependencies):**
    *   在 `pom.xml` (Maven) 或 `build.gradle` (Gradle) 中加入以下依賴：
        *   **Java-Faker:** 用於生成逼真的假資料。
        *   **JDBC Driver:** 對應您所選資料庫的驅動程式 (例如 `mysql-connector-java`)。

    ```xml
    <!-- pom.xml (Maven 範例) -->
    <dependencies>
        <!-- For realistic data generation -->
        <dependency>
            <groupId>com.github.javafaker</groupId>
            <artifactId>javafaker</artifactId>
            <version>1.0.2</version>
        </dependency>
        <!-- For MySQL Connection -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
    </dependencies>
    ```

#### **階段 1: 資料庫與模型層建構 (Database & Model Layer)**

*   [ ] **1. 執行資料庫 Schema (DDL):**
    *   在您的資料庫中執行以下 SQL 指令碼，建立所需的資料表。

    ```sql
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
        FOREIGN KEY (course_id) REFERENCES courses(id),
        UNIQUE KEY unique_enrollment (student_id, course_id) -- 防止學生重複選修
    );
    ```

*   [ ] **2. 建立 Java 模型 (POJO):**
    *   在 `src/main/java/com/yourcompany/datagen/model/` 路徑下，為每個資料表建立對應的 Java 類別。
    *   `Teacher.java` (包含 `id`, `fullName`, `email`)
    *   `Student.java` (包含 `id`, `fullName`, `birthDate`, `email`)
    *   `Course.java` (包含 `id`, `courseName`, `credits`, `teacherId`)
    *   `Enrollment.java` (包含 `id`, `studentId`, `courseId`, `enrollmentDate`)

#### **階段 2: 資料生成邏輯實作 (Data Generation Logic)**

*   [ ] **1. 建立 `DataGenerator` 類別:**
    *   在 `generator` 套件下建立 `DataGenerator.java`。
    *   在建構函式中初始化 `Faker` 物件: `private final Faker faker = new Faker(new Locale("zh-TW"));` (可選地區)。

*   [ ] **2. 實作生成方法:**
    *   `public List<Teacher> generateTeachers(int count)`: 迴圈 `count` 次，每次使用 `faker.name().fullName()` 和 `faker.internet().emailAddress()` 創建 `Teacher` 物件。
    *   `public List<Student> generateStudents(int count)`: 迴圈 `count` 次，使用 `faker.name().fullName()`, `faker.date().birthday()`, 和 `faker.internet().emailAddress()` 創建 `Student` 物件。
    *   `public List<Course> generateCourses(int count, List<Long> teacherIds)`: 迴圈 `count` 次，隨機組合課程名稱 (例如: `faker.educator().course()`)，隨機生成學分數 (1-4)，並從傳入的 `teacherIds` 列表中隨機選取一個 ID。

#### **階段 3: 資料庫存取層實作 (Repository Layer)**

*   [ ] **1. 建立資料庫設定檔:**
    *   在 `resources` 資料夾下建立 `config.properties` 檔案，存放資料庫連線資訊。
    ```properties
    db.url=jdbc:mysql://localhost:3306/school_data
    db.user=your_username
    db.password=your_password
    ```

*   [ ] **2. 建立 `DatabaseManager` 類別:**
    *   負責讀取設定檔並提供 `getConnection()` 方法來建立資料庫連線。

*   [ ] **3. 實作 `Repository` 類別 (含批次處理):**
    *   為每個模型建立一個 `Repository` 類別，例如 `StudentRepository.java`。
    *   實作 `saveBatch(List<T> items)` 方法，這是此任務的效能核心。
    *   **方法內部邏輯:**
        1.  定義 `INSERT` SQL 語句。
        2.  使用 `try-with-resources` 獲取連線和 `PreparedStatement`。
        3.  設定 `connection.setAutoCommit(false)`。
        4.  遍歷傳入的 `items` 列表。
        5.  為每個 `item` 設定 `PreparedStatement` 的參數 (`setString`, `setDate`, 等)。
        6.  呼叫 `ps.addBatch()` 將其加入批次。
        7.  迴圈結束後，呼叫 `ps.executeBatch()` 執行所有操作。
        8.  呼叫 `connection.commit()` 提交事務。
        9.  **(關鍵)** 如果需要取回自動生成的 ID，`PreparedStatement` 需使用 `Statement.RETURN_GENERATED_KEYS` 參數創建。執行後，可透過 `ps.getGeneratedKeys()` 獲取 ID。

#### **階段 4: 主程式流程編排 (Main Application Logic)**

*   [ ] **1. 建立 `Application.java` 主類別:**
    *   建立 `main` 方法作為程式進入點。

*   [ ] **2. 定義常數:**
    *   `TEACHER_COUNT = 100`, `STUDENT_COUNT = 10000`, `COURSE_COUNT = 1000`, `ENROLLMENT_COUNT = 1000000`, `BATCH_SIZE = 5000`。

*   [ ] **3. 實作執行流程:**
    1.  記錄開始時間 `long startTime = System.currentTimeMillis();`。
    2.  初始化 `DataGenerator` 和各個 `Repository`。
    3.  **處理 Teachers:**
        *   `List<Teacher> teachers = generator.generateTeachers(TEACHER_COUNT);`
        *   `List<Long> teacherIds = teacherRepository.saveBatch(teachers);`
        *   列印進度日誌。
    4.  **處理 Students:**
        *   `List<Student> students = generator.generateStudents(STUDENT_COUNT);`
        *   `List<Long> studentIds = studentRepository.saveBatch(students);`
        *   列印進度日誌。
    5.  **處理 Courses:**
        *   `List<Course> courses = generator.generateCourses(COURSE_COUNT, teacherIds);`
        *   `List<Long> courseIds = courseRepository.saveBatch(courses);`
        *   列印進度日誌。
    6.  **處理 Enrollments (分批):**
        *   建立一個空的 `List<Enrollment> enrollmentBatch`。
        *   使用 `for` 迴圈從 1 到 `ENROLLMENT_COUNT`。
        *   在迴圈中，隨機從 `studentIds` 和 `courseIds` 列表中選取 ID，生成隨機日期，創建 `Enrollment` 物件。
        *   將物件加入 `enrollmentBatch`。
        *   `if (i % BATCH_SIZE == 0 || i == ENROLLMENT_COUNT)`:
            *   呼叫 `enrollmentRepository.saveBatch(enrollmentBatch)`。
            *   `enrollmentBatch.clear();` // 清空列表以釋放記憶體。
            *   列印進度，例如 "已插入 ... / 1,000,000 筆選課資料"。
    7.  記錄結束時間，計算並印出總耗時。

#### **階段 5: 測試與驗證 (Testing & Validation)**

*   [ ] **1. 編譯與執行:**
    *   透過 IDE 或 `mvn clean package` 指令編譯專案。
    *   執行生成的主程式。

*   [ ] **2. 監控執行過程:**
    *   觀察控制台輸出的進度日誌，確保程式正常運行。

*   [ ] **3. 資料庫驗證:**
    *   程式執行完畢後，連線到資料庫。
    *   執行 `SELECT COUNT(*) FROM ...` 查詢，確認各表的資料筆數是否符合預期。
    *   隨機抽取幾筆資料，檢查其內容是否合理、外鍵關聯是否正確。