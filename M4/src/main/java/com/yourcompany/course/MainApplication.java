package com.yourcompany.course;

import com.yourcompany.course.ui.MainFrame;
import javax.swing.SwingUtilities;

public class MainApplication {

    public static void main(String[] args) {
        // 使用 SwingUtilities.invokeLater 確保 GUI 在事件分派執行緒中建立與更新
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
