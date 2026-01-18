package org.sxi.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 主窗口类，模拟Android的Activity，作为应用的入口和容器
 */
public class MainActivity extends JFrame {
    // UI常量
    private static final int WINDOW_WIDTH = 1400;
    private static final int WINDOW_HEIGHT = 1000;
    private static final String WINDOW_TITLE = "博客编辑器";
    
    // 内容容器面板（模拟Fragment容器）
    private JPanel contentPanel;
    // 当前显示的Fragment
    private JPanel currentFragment;

    private static MainActivity mainActivity;
    
    /**
     * 构造方法，初始化主窗口
     */

    public static MainActivity getInstance(){
        if (mainActivity == null){
            mainActivity = new MainActivity();
        }

        return mainActivity;
    }

    private MainActivity() {
        initComponents();
        initEvents();
        // 默认显示浏览文章页面
        showFragment(BrowseFragment.getBrowseFragmentInstance());
    }
    
    /**
     * 初始化组件
     */
    private void initComponents() {
        // 设置窗口属性
        setTitle(WINDOW_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示
        setLayout(new BorderLayout());
        
        // 添加导航栏
        add(createNavigationBar(), BorderLayout.NORTH);
        
        // 创建内容容器
        contentPanel = new JPanel();
        contentPanel.setLayout(new CardLayout());
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * 初始化事件
     */
    private void initEvents() {
        // 窗口关闭事件已在initComponents中设置
    }
    
    /**
     * 创建导航栏
     * @return 导航栏组件
     */
    private JMenuBar createNavigationBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 245, 245));
        menuBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // 创建导航按钮
        JButton browseBtn = createNavButton("浏览文章");
        JButton editBtn = createNavButton("新增文章");
        JButton settingsBtn = createNavButton("设置");
        
        // 添加点击事件
        browseBtn.addActionListener(e -> showFragment(BrowseFragment.getBrowseFragmentInstance()));
        editBtn.addActionListener(e -> showFragment(new EditFragment()));
        settingsBtn.addActionListener(e -> showFragment(new SettingsFragment()));
        
        // 添加按钮到导航栏
        menuBar.add(browseBtn);
        menuBar.add(Box.createHorizontalStrut(10));
        menuBar.add(editBtn);
        menuBar.add(Box.createHorizontalStrut(10));
        menuBar.add(settingsBtn);
        
        // 添加空白区域，使按钮靠右
        menuBar.add(Box.createHorizontalGlue());
        
        return menuBar;
    }
    
    /**
     * 创建导航按钮
     * @param text 按钮文本
     * @return 导航按钮
     */
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setBackground(new Color(255, 255, 255));
        button.setForeground(new Color(50, 50, 50));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // 添加悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 240, 240));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 255, 255));
            }
        });
        
        return button;
    }
    
    /**
     * 显示指定的Fragment（模拟）
     * @param fragment 要显示的Fragment（JPanel）
     */
    public void showFragment(JPanel fragment) {
        if (currentFragment != null) {
            contentPanel.remove(currentFragment);
        }
        
        currentFragment = fragment;
        contentPanel.add(fragment);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * 主方法，启动应用
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainActivity activity = new MainActivity();
            activity.setVisible(true);
        });
    }
}