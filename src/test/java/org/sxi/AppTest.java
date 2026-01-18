package org.sxi;


import org.junit.jupiter.api.Test;
import org.sxi.dao.ArticleDataCore;
import org.sxi.ui.MainActivity;
import org.sxi.util.FileUtil;

import javax.swing.*;

/**
 * Unit test for simple App.
 */

public class AppTest {

    @Test
    public void test01() {
        // 在EDT线程中启动UI
        SwingUtilities.invokeLater(() -> {
            MainActivity mainActivity = MainActivity.getInstance();
            mainActivity.setVisible(true);
        });
    }
}
