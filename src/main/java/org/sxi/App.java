package org.sxi;

import org.sxi.dao.ArticleDataCore;
import org.sxi.ui.MainActivity;

import javax.swing.SwingUtilities;

/**
 * 应用入口类
 */
public class App {
    public static void main( String[] args ) {
        // 在EDT线程中启动UI
        SwingUtilities.invokeLater(() -> {
            MainActivity mainActivity = MainActivity.getInstance();
            mainActivity.setVisible(true);
        });
    }
}
