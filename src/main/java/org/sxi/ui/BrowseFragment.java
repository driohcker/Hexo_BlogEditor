package org.sxi.ui;

import org.sxi.dao.ArticleDataCore;
import org.sxi.dao.PropertiesDataCore;
import org.sxi.vo.Article;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
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

    // 搜索相关组件
    private JTextField searchTextField;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> tagComboBox;
    private JButton searchButton;
    private JButton resetButton;
    
    // 分页相关组件
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages;
    private List<Article> currentArticles;
    private JButton firstPageButton;
    private JButton prevPageButton;
    private JButton nextPageButton;
    private JButton lastPageButton;
    private JTextField pageTextField;
    private JLabel pageInfoLabel;

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

        // 创建搜索面板
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setAlignmentX(LEFT_ALIGNMENT);

        // 创建搜索栏
        JPanel searchBar = new JPanel();
        searchBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchBar.setBackground(Color.WHITE);

        // 标题搜索
        searchTextField = new JTextField(30);
        searchTextField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchTextField.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 分类选择
        categoryComboBox = new JComboBox<>();
        categoryComboBox.setPreferredSize(new Dimension(150, 30));
        categoryComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 标签选择
        tagComboBox = new JComboBox<>();
        tagComboBox.setPreferredSize(new Dimension(150, 30));
        tagComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 搜索按钮
        searchButton = new JButton("搜索");
        searchButton.setPreferredSize(new Dimension(80, 30));
        searchButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        searchButton.setBackground(new Color(66, 133, 244));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createEmptyBorder());
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 重置按钮
        resetButton = new JButton("重置");
        resetButton.setPreferredSize(new Dimension(80, 30));
        resetButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        resetButton.setBackground(new Color(200, 200, 200));
        resetButton.setForeground(Color.BLACK);
        resetButton.setBorder(BorderFactory.createEmptyBorder());
        resetButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 刷新按钮
        button = new JButton("刷新");
        button.setPreferredSize(new Dimension(80, 30));
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setBackground(new Color(76, 175, 80));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 添加组件到搜索栏
        searchBar.add(new JLabel("标题搜索:"));
        searchBar.add(searchTextField);
        searchBar.add(new JLabel("分类:"));
        searchBar.add(categoryComboBox);
        searchBar.add(new JLabel("标签:"));
        searchBar.add(tagComboBox);
        searchBar.add(searchButton);
        searchBar.add(resetButton);
        searchBar.add(button);

        searchPanel.add(searchBar);

        // 创建文章列表容器
        articlesPanel = new JPanel();
        articlesPanel.setLayout(new BoxLayout(articlesPanel, BoxLayout.Y_AXIS));
        articlesPanel.setBackground(Color.WHITE);
        articlesPanel.setAlignmentX(LEFT_ALIGNMENT);

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(articlesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(0, 650)); // 固定显示区域高度
        scrollPane.setMinimumSize(new Dimension(0, 650)); // 固定显示区域高度
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);

        // 美化滚动条
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));

        // 添加组件到卡片面板
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(15));
        cardPanel.add(searchPanel);
        cardPanel.add(Box.createVerticalStrut(25));
        cardPanel.add(scrollPane);
        cardPanel.add(Box.createVerticalStrut(15));
        cardPanel.add(createPaginationPanel());

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

        searchButton.addActionListener(e -> {
            performSearch();
        });

        resetButton.addActionListener(e -> {
            resetSearch();
        });
    }

    private void initData(){
        // 初始化分类选择框
        categoryComboBox.addItem("全部分类");
        ArticleDataCore.getCategoriesDataCore().forEach(category -> {
            categoryComboBox.addItem(category);
        });

        // 初始化标签选择框
        tagComboBox.addItem("全部标签");
        ArticleDataCore.getTagsDataCore().forEach(tag -> {
            tagComboBox.addItem(tag);
        });

        // 初始化文章列表（分页）
        currentPage = 1;
        currentArticles = new ArrayList<>();
        ArticleDataCore.getArticleDataCore().forEach((k, v) -> currentArticles.add((Article) v));
        loadArticles();
    }

    /**
     * 执行搜索
     */
    private void performSearch() {
        String keyword = searchTextField.getText().trim();
        String category = categoryComboBox.getSelectedItem().toString();
        String tag = tagComboBox.getSelectedItem().toString();

        // 获取符合条件的文章
        List<Article> filteredArticles = new ArrayList<>();

        // 首先根据关键词搜索
        if (!keyword.isEmpty()) {
            filteredArticles = ArticleDataCore.searchArticles(keyword);
        } else {
            // 如果没有关键词，获取所有文章
            List<Article> allArticles = new ArrayList<>();
            ArticleDataCore.getArticleDataCore().forEach((k, v) -> allArticles.add((Article) v));
            filteredArticles = allArticles;
        }

        // 然后根据分类过滤
        if (!"全部分类".equals(category)) {
            List<Article> categoryFiltered = new ArrayList<>();
            for (Article article : filteredArticles) {
                if (article.getCategories() != null && article.getCategories().contains(category)) {
                    categoryFiltered.add(article);
                }
            }
            filteredArticles = categoryFiltered;
        }

        // 最后根据标签过滤
        if (!"全部标签".equals(tag)) {
            List<Article> tagFiltered = new ArrayList<>();
            for (Article article : filteredArticles) {
                if (article.getTags() != null && article.getTags().contains(tag)) {
                    tagFiltered.add(article);
                }
            }
            filteredArticles = tagFiltered;
        }

        // 保存搜索结果并重置分页
        currentArticles = filteredArticles;
        currentPage = 1;
        
        // 加载第一页数据
        loadArticles();
    }

    /**
     * 重置搜索
     */
    private void resetSearch() {
        searchTextField.setText("");
        categoryComboBox.setSelectedIndex(0);
        tagComboBox.setSelectedIndex(0);
        
        // 重置为所有文章并重新分页
        currentArticles = new ArrayList<>();
        ArticleDataCore.getArticleDataCore().forEach((k, v) -> currentArticles.add((Article) v));
        currentPage = 1;
        loadArticles();
    }

    /**
     * 创建分页组件面板
     */
    private JPanel createPaginationPanel() {
        JPanel paginationPanel = new JPanel();
        paginationPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        paginationPanel.setBackground(Color.WHITE);
        paginationPanel.setAlignmentX(LEFT_ALIGNMENT);
        
        // 第一页按钮
        firstPageButton = new JButton("首页");
        firstPageButton.setPreferredSize(new Dimension(80, 30));
        firstPageButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        firstPageButton.setBackground(new Color(240, 240, 240));
        firstPageButton.setForeground(Color.BLACK);
        firstPageButton.setBorder(BorderFactory.createEmptyBorder());
        firstPageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // 上一页按钮
        prevPageButton = new JButton("上一页");
        prevPageButton.setPreferredSize(new Dimension(80, 30));
        prevPageButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        prevPageButton.setBackground(new Color(240, 240, 240));
        prevPageButton.setForeground(Color.BLACK);
        prevPageButton.setBorder(BorderFactory.createEmptyBorder());
        prevPageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // 页码输入框
        pageTextField = new JTextField(5);
        pageTextField.setHorizontalAlignment(JTextField.CENTER);
        pageTextField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        pageTextField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));
        
        // 页码信息
        pageInfoLabel = new JLabel("第 1 页，共 1 页");
        pageInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        pageInfoLabel.setForeground(new Color(60, 60, 60));
        
        // 下一页按钮
        nextPageButton = new JButton("下一页");
        nextPageButton.setPreferredSize(new Dimension(80, 30));
        nextPageButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        nextPageButton.setBackground(new Color(240, 240, 240));
        nextPageButton.setForeground(Color.BLACK);
        nextPageButton.setBorder(BorderFactory.createEmptyBorder());
        nextPageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // 最后一页按钮
        lastPageButton = new JButton("末页");
        lastPageButton.setPreferredSize(new Dimension(80, 30));
        lastPageButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        lastPageButton.setBackground(new Color(240, 240, 240));
        lastPageButton.setForeground(Color.BLACK);
        lastPageButton.setBorder(BorderFactory.createEmptyBorder());
        lastPageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // 添加组件到面板
        paginationPanel.add(firstPageButton);
        paginationPanel.add(prevPageButton);
        paginationPanel.add(new JLabel("页码:"));
        paginationPanel.add(pageTextField);
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(nextPageButton);
        paginationPanel.add(lastPageButton);
        
        // 添加事件监听
        firstPageButton.addActionListener(e -> goToFirstPage());
        prevPageButton.addActionListener(e -> goToPrevPage());
        nextPageButton.addActionListener(e -> goToNextPage());
        lastPageButton.addActionListener(e -> goToLastPage());
        
        // 页码输入框回车事件
        pageTextField.addActionListener(e -> {
            try {
                int page = Integer.parseInt(pageTextField.getText().trim());
                if (page >= 1 && page <= totalPages) {
                    currentPage = page;
                    loadArticles();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的页码", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        return paginationPanel;
    }
    
    /**
     * 跳转到首页
     */
    private void goToFirstPage() {
        if (currentPage > 1) {
            currentPage = 1;
            loadArticles();
        }
    }
    
    /**
     * 跳转到上一页
     */
    private void goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            loadArticles();
        }
    }
    
    /**
     * 跳转到下一页
     */
    private void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadArticles();
        }
    }
    
    /**
     * 跳转到末页
     */
    private void goToLastPage() {
        if (currentPage < totalPages) {
            currentPage = totalPages;
            loadArticles();
        }
    }
    
    /**
     * 加载文章列表（分页）
     */
    private void loadArticles() {
        if (currentArticles == null || currentArticles.isEmpty()) {
            articlesPanel.removeAll();
            articlesPanel.revalidate();
            articlesPanel.repaint();
            return;
        }
        
        // 清空旧的文章列表
        articlesPanel.removeAll();
        
        // 计算当前页的文章范围
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, currentArticles.size());
        
        // 添加当前页的文章
        for (int i = startIndex; i < endIndex; i++) {
            articlesPanel.add(new ArticleRow(currentArticles.get(i)));
        }
        
        // 更新分页信息
        totalPages = (currentArticles.size() + pageSize - 1) / pageSize;
        pageTextField.setText(String.valueOf(currentPage));
        pageInfoLabel.setText("第 " + currentPage + " 页，共 " + totalPages + " 页");
        
        // 更新按钮状态
        firstPageButton.setEnabled(currentPage > 1);
        prevPageButton.setEnabled(currentPage > 1);
        nextPageButton.setEnabled(currentPage < totalPages);
        lastPageButton.setEnabled(currentPage < totalPages);
        
        // 刷新UI
        articlesPanel.revalidate();
        articlesPanel.repaint();
    }

    public void refreshData(){
        // 重新初始化文章数据（会获取最新配置路径）
        ArticleDataCore.reInitArtcleData();
        // 重新初始化分类数据（会获取最新配置路径）
        ArticleDataCore.reInitcategoriesData();
        
        // 清空旧的分类和标签选择框
        categoryComboBox.removeAllItems();
        tagComboBox.removeAllItems();
        
        // 重新初始化分类选择框
        categoryComboBox.addItem("全部分类");
        ArticleDataCore.getCategoriesDataCore().forEach(category -> {
            categoryComboBox.addItem(category);
        });

        // 重新初始化标签选择框
        tagComboBox.addItem("全部标签");
        ArticleDataCore.getTagsDataCore().forEach(tag -> {
            tagComboBox.addItem(tag);
        });
        
        // 重置分页
        currentPage = 1;
        
        // 获取所有文章
        currentArticles = new ArrayList<>();
        ArticleDataCore.getArticleDataCore().forEach((k, v) -> currentArticles.add((Article) v));
        
        // 加载第一页文章
        loadArticles();
        
        System.out.println("数据已刷新，使用的配置路径：" + PropertiesDataCore.getStringProperty("post.root.path"));
    }
}