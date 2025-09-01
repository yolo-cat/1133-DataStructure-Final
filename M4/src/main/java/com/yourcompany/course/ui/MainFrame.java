package com.yourcompany.course.ui;

import com.yourcompany.course.benchmark.BenchmarkRunner;
import com.yourcompany.course.generator.DataGenerator;
import com.yourcompany.course.model.dto.BenchmarkResult;
import com.yourcompany.course.repository.DataRepository;
import com.yourcompany.course.search.InMemorySearchService;
import com.yourcompany.course.search.SearchService;
import com.yourcompany.course.search.SqlSearchService;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainFrame extends JFrame {

    // Services and Repositories
    private final SearchService sqlService;
    private final InMemorySearchService inMemoryService;
    private final DataGenerator dataGenerator;

    // UI Components
    private JTextArea resultArea;
    private JLabel statusLabel;
    private JRadioButton sqlRadioButton;
    private JRadioButton inMemoryRadioButton;
    private JTextField idField;
    private JComboBox<String> scaleComboBox;

    public MainFrame() {
        // 1. 實例化服務
        this.sqlService = new SqlSearchService();
        this.inMemoryService = new InMemorySearchService();
        this.dataGenerator = new DataGenerator(new DataRepository());

        // 2. 設定主視窗
        setTitle("課程選課系統效能測試");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 3. 建立並添加元件
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(createDataGenerationPanel());
        topPanel.add(createControlPanel());

        add(topPanel, BorderLayout.NORTH);
        add(createResultPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
        add(createSchemeSelectionPanel(), BorderLayout.WEST);

        // 4. 載入初始資料
        loadInMemoryData();
    }

    private JPanel createDataGenerationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("資料生成"));

        panel.add(new JLabel("選擇資料規模:"));
        String[] scales = {"1萬", "10萬", "100萬"};
        scaleComboBox = new JComboBox<>(scales);
        panel.add(scaleComboBox);

        JButton generateDataBtn = new JButton("生成資料並載入");
        generateDataBtn.addActionListener(e -> generateData());
        panel.add(generateDataBtn);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("查詢控制"));

        panel.add(new JLabel("ID (學生/課程):"));
        idField = new JTextField(10);
        panel.add(idField);

        JButton findCoursesBtn = new JButton("查詢學生選課");
        findCoursesBtn.addActionListener(e -> executeSearch(SearchOperation.FIND_COURSES_BY_STUDENT));

        JButton findStudentsBtn = new JButton("查詢課程選課學生");
        findStudentsBtn.addActionListener(e -> executeSearch(SearchOperation.FIND_STUDENTS_BY_COURSE));

        JButton findTopCoursesBtn = new JButton("查詢熱門課程");
        findTopCoursesBtn.addActionListener(e -> executeSearch(SearchOperation.FIND_TOP_10_COURSES));

        panel.add(findCoursesBtn);
        panel.add(findStudentsBtn);
        panel.add(findTopCoursesBtn);

        return panel;
    }

    private JScrollPane createResultPanel() {
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("日誌、查詢結果與效能報告"));
        return scrollPane;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("歡迎使用系統。請先生成資料。");
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.add(statusLabel);
        return panel;
    }

    private JPanel createSchemeSelectionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("方案選擇"));

        sqlRadioButton = new JRadioButton("方案A: SQL", true);
        inMemoryRadioButton = new JRadioButton("方案B: HashMap");

        ButtonGroup group = new ButtonGroup();
        group.add(sqlRadioButton);
        group.add(inMemoryRadioButton);

        panel.add(sqlRadioButton);
        panel.add(inMemoryRadioButton);

        return panel;
    }

    private void generateData() {
        String selected = (String) scaleComboBox.getSelectedItem();
        if (selected == null) return;

        int scale = 10000;
        if (selected.equals("10萬")) scale = 100000;
        if (selected.equals("100萬")) scale = 1000000;

        resultArea.setText(""); // 清空日誌區域
        statusLabel.setText("正在生成資料，請查看日誌輸出...");

        int finalScale = scale;
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                dataGenerator.generate(finalScale, this::publish);
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    resultArea.append(message + "\n");
                }
            }

            @Override
            protected void done() {
                statusLabel.setText("資料生成完畢。現在開始自動載入記憶體資料...");
                loadInMemoryData(); // 生成後自動重新載入
            }
        }.execute();
    }

    private void loadInMemoryData() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                resultArea.append("\n開始將資料載入到記憶體 (HashMap)...
");
                inMemoryService.loadData();
                return null;
            }

            @Override
            protected void done() {
                resultArea.append("記憶體資料載入完成！\n");
                statusLabel.setText("系統就緒。請執行查詢。");
            }
        }.execute();
    }

    private enum SearchOperation {
        FIND_COURSES_BY_STUDENT,
        FIND_STUDENTS_BY_COURSE,
        FIND_TOP_10_COURSES
    }

    private void executeSearch(SearchOperation operation) {
        long id = 0;
        if (operation != SearchOperation.FIND_TOP_10_COURSES) {
            try {
                id = Long.parseLong(idField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "請輸入有效的數字ID。", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        SearchService currentService = sqlRadioButton.isSelected() ? sqlService : inMemoryService;
        statusLabel.setText("正在使用 " + currentService.getServiceName() + " 執行查詢...");
        resultArea.setText("");

        long finalId = id;

        new SwingWorker<List<?>, Void>() {
            private BenchmarkResult benchmarkResult;

            @Override
            protected List<?> doInBackground() throws Exception {
                List<?> results;
                List<Long> testIds = Collections.nCopies(1000, finalId);

                switch (operation) {
                    case FIND_COURSES_BY_STUDENT:
                        benchmarkResult = BenchmarkRunner.runFindCoursesByStudent(currentService, testIds);
                        results = currentService.findCoursesByStudent(finalId);
                        break;
                    case FIND_STUDENTS_BY_COURSE:
                        benchmarkResult = BenchmarkRunner.runFindStudentsByCourse(currentService, testIds);
                        results = currentService.findStudentsByCourse(finalId);
                        break;
                    case FIND_TOP_10_COURSES:
                        long startTime = System.nanoTime();
                        for(int i=0; i<1000; i++) currentService.findTop10PopularCourses();
                        long totalTime = System.nanoTime() - startTime;
                        benchmarkResult = new BenchmarkResult(totalTime, 1000);
                        results = currentService.findTop10PopularCourses();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + operation);
                }
                return results;
            }

            @Override
            protected void done() {
                try {
                    List<?> results = get();
                    StringBuilder sb = new StringBuilder();
                    sb.append("====== 查詢結果 ======\n");
                    if (results.isEmpty()) {
                        sb.append("未找到結果。\n");
                    } else {
                        results.forEach(r -> sb.append(r.toString()).append("\n"));
                    }

                    sb.append("\n====== 效能報告 ======\n");
                    sb.append(benchmarkResult.toString()).append("\n");

                    resultArea.setText(sb.toString());
                    statusLabel.setText("查詢完成。");

                } catch (InterruptedException | ExecutionException e) {
                    resultArea.setText("執行查詢時發生錯誤: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
