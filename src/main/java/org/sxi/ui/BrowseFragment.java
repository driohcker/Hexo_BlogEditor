package org.sxi.ui;

import org.sxi.dao.ArticleDataCore;
import org.sxi.dao.PropertiesDataCore;
import org.sxi.vo.Article;

import javax.swing.*;
import java.awt.*;

/**
 * 浏览文章页面Fragment，模拟Android的Fragment
 */
public class BrowseFragment extends JPanel {
    // UI常量
    private static final int CARD_WIDTH = 1400;
    private static final int CARD_PADDING = 30;

    static JPanel articlesPanel;

    JButton button;

    private static BrowseFragment browseFragment;

    private BrowseFragment() {
        initComponents();
        initEvents();
        initData();
    }

    public static BrowseFragment getBrowseFragmentInstance() {
        if (browseFragment == null) {
            browseFragment = new BrowseFragment();
        }
        return browseFragment;
    }

    /**
     * 初始化组件
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // 创建中央内容卡片
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(CARD_PADDING, CARD_PADDING, CARD_PADDING, CARD_PADDING)
        ));
        cardPanel.setPreferredSize(new Dimension(CARD_WIDTH, 0));

        // 创建标题
        JLabel titleLabel = new JLabel("文章列表");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 30, 30));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        // 创建文章列表容器
        articlesPanel = new JPanel();
        articlesPanel.setLayout(new BoxLayout(articlesPanel, BoxLayout.Y_AXIS));
        articlesPanel.setBackground(Color.WHITE);
        articlesPanel.setAlignmentX(LEFT_ALIGNMENT);

        button = new JButton("刷新");

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(articlesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);

        // 美化滚动条
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));

        // 添加组件到卡片面板
        cardPanel.add(titleLabel);
        cardPanel.add(button);
        cardPanel.add(Box.createVerticalStrut(25));
        cardPanel.add(scrollPane);

        // 创建包装面板，使卡片居中
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));
        wrapperPanel.setBackground(new Color(248, 249, 250));
        wrapperPanel.add(Box.createHorizontalGlue());
        wrapperPanel.add(cardPanel);
        wrapperPanel.add(Box.createHorizontalGlue());

        // 添加到主面板
        add(wrapperPanel, BorderLayout.CENTER);
    }

    /**
     * 初始化事件
     */
    private void initEvents() {
        button.addActionListener(e -> {
            refreshData();
        });
    }

    private void initData(){
        ArticleDataCore.getArticleDataCore().forEach((k, v) -> {
            articlesPanel.add(new ArticleRow((Article) v));
        });
    }

    public void refreshData(){
        // 重新初始化文章数据（会获取最新配置路径）
        ArticleDataCore.reInitArtcleData();
        // 重新初始化分类数据（会获取最新配置路径）
        ArticleDataCore.reInitcategoriesData();
        // 清空旧的文章列表
        articlesPanel.removeAll();
        // 添加新的文章列表
        ArticleDataCore.getArticleDataCore().forEach((k, v) -> {
            articlesPanel.add(new ArticleRow((Article) v));
        });
        // 刷新UI
        articlesPanel.revalidate();
        articlesPanel.repaint();
        System.out.println("数据已刷新，使用的配置路径：" + PropertiesDataCore.getStringProperty("post.root.path"));
    }
}