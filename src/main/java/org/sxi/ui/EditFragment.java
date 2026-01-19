package org.sxi.ui;

import org.sxi.biz.ArticleBiz;
import org.sxi.biz.BizException;
import org.sxi.dao.ArticleDataCore;
import org.sxi.util.ArticleUtil;
import org.sxi.vo.Article;
import org.sxi.vo.Result;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;

/**
 * 文章编辑页面Fragment，模拟Android的Fragment
 */
public class EditFragment extends JPanel {

    private static final int CARD_WIDTH = 900;
    private static final int CARD_PADDING = 30;

    private static final int FIELD_HEIGHT = 40;
    private static final int LEFT_PANEL_WIDTH = 280;

    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 45;

    private Article article = new Article();

    JTextField titleTextField;

    JButton saveButton;

    JTextArea textArea;

    ArticleBiz articleBiz = new ArticleBiz();

    boolean isNew = true;

    public EditFragment() {
        initComponents();
        initStyles();
        initEvents();
        initData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // ===== 卡片容器 =====
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(CARD_PADDING, CARD_PADDING, CARD_PADDING, CARD_PADDING)
        ));
        cardPanel.setPreferredSize(new Dimension(CARD_WIDTH, 0));

        // ===== 页面标题 =====
        JLabel titleLabel = new JLabel("编辑文章");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 30, 30));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        // ===== 主内容区域（左右布局）=====
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setAlignmentX(LEFT_ALIGNMENT);

        // 左侧：文章元信息
        JPanel leftPanel = createLeftMetaPanel();

        // 右侧：正文编辑器
        JPanel rightPanel = createContentEditorPanel();

        contentPanel.add(leftPanel);
        contentPanel.add(Box.createHorizontalStrut(30));
        contentPanel.add(rightPanel);

        // ===== 组装 =====
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(25));
        cardPanel.add(contentPanel);

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));
        wrapperPanel.setBackground(new Color(248, 249, 250));
        wrapperPanel.add(Box.createHorizontalGlue());
        wrapperPanel.add(cardPanel);
        wrapperPanel.add(Box.createHorizontalGlue());

        add(wrapperPanel, BorderLayout.CENTER);
    }

    private void initEvents() {

        saveButton.addActionListener(e ->{
            //JOptionPane.showMessageDialog(EditFragment.this,"文章保存成功！", "成功", JOptionPane.INFORMATION_MESSAGE)

            article.setTitle(titleTextField.getText());

            if(isNew){
                article.setFileName(article.getTitle());
            }else{
                article.setFileName(article.getFileName());
            }

            article.setCategories(Collections.singletonList(categoryComboBox.getSelectedItem().toString()));
            article.setTags(tagsList.isEmpty() ? Collections.emptyList() : new java.util.ArrayList<>(tagsList));
            article.setDate(new Date());
            article.setContent(textArea.getText().trim());

            if (isNew){
                articleBiz.addArticle(article);
            }else{
                articleBiz.editArticle(article);
            }

            MainActivity.getInstance().showFragment(BrowseFragment.getBrowseFragmentInstance());
            BrowseFragment.getBrowseFragmentInstance().refreshData();

        });
    }

    private void initStyles(){
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(new Color(52, 108, 204));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(new Color(66, 133, 244));
            }
        });
    }

    public void initData(){
        ArticleDataCore.categoriesDataCore.forEach(category -> {
            categoryComboBox.addItem(category);
        });
    }

    public Result setData(Article article){

        if (article == null) {
            throw new BizException("文章不能为空");
        }

        this.isNew = false;

        this.article = article;

        titleTextField.setText(article.getTitle());

        categoryComboBox.setSelectedItem(article.getCategories().get(0));

        // 清空现有标签
        tagsList.clear();
        tagDisplayPanel.removeAll();
        
        // 添加文章标签
        if (article.getTags() != null && !article.getTags().isEmpty()) {
            tagsList.addAll(article.getTags());
            for (String tag : article.getTags()) {
                addTagToDisplay(tag);
            }
        }

        try {
            textArea.setText(ArticleUtil.getArticleContent(article));
        } catch (IOException e) {
            throw new BizException("读取文章内容失败");
        }

        return Result.ok("初始化数据成功");
    }

    // ================= 左侧：元信息 =================

    // 分类相关组件
    private JComboBox<String> categoryComboBox;
    private JButton addCategoryButton;
    private JPanel newCategoryPanel;
    private JTextField newCategoryTextField;
    private JButton confirmNewCategoryButton;

    private JPanel createLeftMetaPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, 0));
        panel.setMaximumSize(new Dimension(LEFT_PANEL_WIDTH, Integer.MAX_VALUE));
        panel.setAlignmentX(LEFT_ALIGNMENT);

        titleTextField = new JTextField();
        categoryComboBox = new JComboBox<>();

        panel.add(createFormField("标题", titleTextField));
        panel.add(createCategoryPanel()); // 使用自定义的分类面板
        panel.add(createTagManagementPanel());

        panel.add(Box.createVerticalGlue());

        JButton saveButton = createSaveButton();
        saveButton.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(saveButton);

        return panel;
    }

    // ================= 右侧：正文编辑 =================

    private JEditorPane previewPane; // 预览区域
    private JSplitPane splitPane; // 分隔面板
    private JCheckBox previewToggle; // 预览开关

    private JPanel createContentEditorPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel editorLabel = new JLabel("内容");
        editorLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        editorLabel.setForeground(new Color(60, 60, 60));
        editorLabel.setAlignmentX(LEFT_ALIGNMENT);

        // 添加MD格式工具栏
        JPanel toolbarPanel = createMarkdownToolbar();
        toolbarPanel.setAlignmentX(LEFT_ALIGNMENT);

        // 创建预览开关
        JPanel togglePanel = new JPanel();
        togglePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        togglePanel.setBackground(Color.WHITE);
        togglePanel.setAlignmentX(LEFT_ALIGNMENT);

        previewToggle = new JCheckBox("实时预览");
        previewToggle.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        previewToggle.setForeground(new Color(60, 60, 60));
        previewToggle.setBackground(Color.WHITE);
        previewToggle.setSelected(false); // 默认关闭预览
        previewToggle.addActionListener(e -> togglePreview());
        togglePanel.add(previewToggle);

        // 左侧：原文本编辑器
        textArea = new JTextArea();
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updatePreview();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updatePreview();
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updatePreview();
            }
        });

        JScrollPane editorScrollPane = new JScrollPane(textArea);

        // 右侧：实时预览区域
        previewPane = new JEditorPane();
        previewPane.setContentType("text/html");
        previewPane.setEditable(false);
        previewPane.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        previewPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JScrollPane previewScrollPane = new JScrollPane(previewPane);

        // 创建分隔面板
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorScrollPane, previewScrollPane);
        splitPane.setPreferredSize(new Dimension(0, 500));
        splitPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        splitPane.setDividerSize(6);
        splitPane.setDividerLocation(0.5); // 初始分割比例
        splitPane.setAlignmentX(LEFT_ALIGNMENT);

        // 初始隐藏预览区域
        splitPane.setRightComponent(null);

        JLabel markdownHint = new JLabel("支持 Markdown 语法");
        markdownHint.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        markdownHint.setForeground(new Color(120, 120, 120));
        markdownHint.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(editorLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(toolbarPanel); // 添加工具栏
        panel.add(togglePanel); // 添加预览开关
        panel.add(Box.createVerticalStrut(8));
        panel.add(splitPane);
        panel.add(Box.createVerticalStrut(8));
        panel.add(markdownHint);

        return panel;
    }

    /**
     * 创建Markdown格式工具栏
     */
    private JPanel createMarkdownToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolbar.setBackground(new Color(248, 249, 250));
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // 标题按钮
        addToolbarButton(toolbar, "H1", () -> insertHeader(1));
        addToolbarButton(toolbar, "H2", () -> insertHeader(2));
        addToolbarButton(toolbar, "H3", () -> insertHeader(3));
        addToolbarButton(toolbar, "H4", () -> insertHeader(4));
        addToolbarButton(toolbar, "H5", () -> insertHeader(5));
        addToolbarButton(toolbar, "H6", () -> insertHeader(6));

        toolbar.add(Box.createHorizontalStrut(10));

        // 文本格式化按钮
        addToolbarButton(toolbar, "B", () -> wrapTextWith("**", "**")); // 粗体
        addToolbarButton(toolbar, "I", () -> wrapTextWith("*", "*"));   // 斜体
        addToolbarButton(toolbar, "BI", () -> wrapTextWith("***", "***")); // 粗斜体
        addToolbarButton(toolbar, "`", () -> wrapTextWith("`", "`"));   // 行内代码

        toolbar.add(Box.createHorizontalStrut(10));

        // 列表按钮
        addToolbarButton(toolbar, "UL", () -> insertListItem("- ")); // 无序列表
        addToolbarButton(toolbar, "OL", () -> insertListItem("1. ")); // 有序列表

        toolbar.add(Box.createHorizontalStrut(10));

        // 其他格式按钮
        addToolbarButton(toolbar, "Link", () -> insertLink());
        addToolbarButton(toolbar, "Image", () -> insertImage());
        addToolbarButton(toolbar, "Quote", () -> insertText("> "));
        addToolbarButton(toolbar, "Code", () -> insertCodeBlock());
        addToolbarButton(toolbar, "---", () -> insertText("\n---\n"));

        return toolbar;
    }

    /**
     * 添加工具栏按钮
     */
    private void addToolbarButton(JPanel toolbar, String text, Runnable action) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(40, 25));
        button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(60, 60, 60));
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        toolbar.add(button);
    }

    /**
     * 插入标题
     */
    private void insertHeader(int level) {
        String prefix = "#".repeat(level) + " ";
        String selectedText = textArea.getSelectedText();
        
        if (selectedText != null) {
            replaceSelectedText(prefix + selectedText);
        } else {
            insertText(prefix);
        }
    }

    /**
     * 用指定的前后缀包裹选中的文字
     */
    private void wrapTextWith(String prefix, String suffix) {
        String selectedText = textArea.getSelectedText();
        int start = textArea.getSelectionStart();
        int end = textArea.getSelectionEnd();
        
        if (selectedText != null) {
            replaceSelectedText(prefix + selectedText + suffix);
        } else {
            insertText(prefix + suffix);
            textArea.setCaretPosition(start + prefix.length());
        }
    }

    /**
     * 插入列表项
     */
    private void insertListItem(String prefix) {
        int start = textArea.getSelectionStart();
        int lineStart = 0;
        try {
            lineStart = textArea.getLineStartOffset(textArea.getLineOfOffset(start));
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        // 在当前行开始处插入列表前缀
        textArea.insert(prefix, lineStart);
        textArea.setCaretPosition(lineStart + prefix.length());
    }

    /**
     * 插入链接
     */
    private void insertLink() {
        String selectedText = textArea.getSelectedText();
        String linkText = selectedText != null ? selectedText : "链接文本";
        String linkUrl = "http://example.com";
        
        wrapTextWith("[", "]()", linkText, linkUrl);
    }

    /**
     * 插入图片
     */
    private void insertImage() {
        String selectedText = textArea.getSelectedText();
        String altText = selectedText != null ? selectedText : "图片描述";
        String imageUrl = "http://example.com/image.jpg";
        
        wrapTextWith("![", "]()", altText, imageUrl);
    }

    /**
     * 插入代码块
     */
    private void insertCodeBlock() {
        String codeTemplate = "```language\n你的代码\n```";
        insertText(codeTemplate);
        textArea.setCaretPosition(textArea.getCaretPosition() - codeTemplate.length() + 4);
    }

    /**
     * 插入文本
     */
    private void insertText(String text) {
        textArea.insert(text, textArea.getCaretPosition());
        updatePreview();
    }

    /**
     * 替换选中的文本
     */
    private void replaceSelectedText(String replacement) {
        int start = textArea.getSelectionStart();
        textArea.replaceSelection(replacement);
        textArea.setSelectionStart(start);
        textArea.setSelectionEnd(start + replacement.length());
        updatePreview();
    }

    /**
     * 用指定的前后缀包裹，并在中间插入分隔的内容
     */
    private void wrapTextWith(String prefix, String suffix, String content1, String content2) {
        int start = textArea.getSelectionStart();
        int end = textArea.getSelectionEnd();
        String replacement = prefix + content1 + suffix.substring(0, 1) + content2 + suffix.substring(1);
        
        if (textArea.getSelectedText() != null) {
            textArea.replaceSelection(replacement);
        } else {
            textArea.insert(replacement, start);
        }
        
        // 将光标定位到第一个内容的末尾
        textArea.setCaretPosition(start + prefix.length() + content1.length());
        updatePreview();
    }

    /**
     * 切换预览功能
     */
    private void togglePreview() {
        if (previewToggle.isSelected()) {
            // 开启预览
            splitPane.setRightComponent(new JScrollPane(previewPane));
            updatePreview();
            splitPane.setDividerLocation(0.5); // 重置分隔位置
        } else {
            // 关闭预览
            splitPane.setRightComponent(null);
        }
    }

    /**
     * 标签管理面板 - 输入框
     */
    private JTextField tagInputField;
    /**
     * 标签管理面板 - 标签展示区
     */
    private JPanel tagDisplayPanel;
    /**
     * 已添加的标签列表
     */
    private java.util.List<String> tagsList;

    /**
     * 创建分类管理面板
     */
    private JPanel createCategoryPanel() {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setAlignmentX(LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT + 60));

        JLabel fieldLabel = new JLabel("分类");
        fieldLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        fieldLabel.setForeground(new Color(60, 60, 60));
        fieldLabel.setAlignmentX(LEFT_ALIGNMENT);

        // 创建分类选择框和添加按钮的容器
        JPanel categoryContainer = new JPanel();
        categoryContainer.setLayout(new BoxLayout(categoryContainer, BoxLayout.X_AXIS));
        categoryContainer.setBackground(Color.WHITE);
        categoryContainer.setAlignmentX(LEFT_ALIGNMENT);

        // 设置分类选择框样式
        categoryComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        categoryComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 0),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        categoryComboBox.setBackground(Color.WHITE);
        categoryComboBox.setPreferredSize(new Dimension(0, FIELD_HEIGHT));
        categoryComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT));
        categoryComboBox.setAlignmentX(LEFT_ALIGNMENT);

        // 创建添加分类按钮（方形加号按钮）
        addCategoryButton = new JButton("+");
        addCategoryButton.setPreferredSize(new Dimension(FIELD_HEIGHT, FIELD_HEIGHT));
        addCategoryButton.setMaximumSize(new Dimension(FIELD_HEIGHT, FIELD_HEIGHT));
        addCategoryButton.setFont(new Font("微软雅黑", Font.BOLD, 18));
        addCategoryButton.setBackground(new Color(66, 133, 244));
        addCategoryButton.setForeground(Color.WHITE);
        addCategoryButton.setBorder(BorderFactory.createEmptyBorder());
        addCategoryButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addCategoryButton.setFocusPainted(false);

        // 创建新分类输入面板（默认隐藏）
        newCategoryPanel = new JPanel();
        newCategoryPanel.setLayout(new BoxLayout(newCategoryPanel, BoxLayout.X_AXIS));
        newCategoryPanel.setBackground(Color.WHITE);
        newCategoryPanel.setAlignmentX(LEFT_ALIGNMENT);
        newCategoryPanel.setVisible(false);

        // 创建新分类输入框
        newCategoryTextField = new JTextField();
        newCategoryTextField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        newCategoryTextField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        newCategoryTextField.setPreferredSize(new Dimension(0, FIELD_HEIGHT));
        newCategoryTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT));
        newCategoryTextField.setAlignmentX(LEFT_ALIGNMENT);

        // 创建确认按钮（方形√按钮）
        confirmNewCategoryButton = new JButton("√");
        confirmNewCategoryButton.setPreferredSize(new Dimension(FIELD_HEIGHT, FIELD_HEIGHT));
        confirmNewCategoryButton.setMaximumSize(new Dimension(FIELD_HEIGHT, FIELD_HEIGHT));
        confirmNewCategoryButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        confirmNewCategoryButton.setBackground(new Color(76, 175, 80));
        confirmNewCategoryButton.setForeground(Color.WHITE);
        confirmNewCategoryButton.setBorder(BorderFactory.createEmptyBorder());
        confirmNewCategoryButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirmNewCategoryButton.setFocusPainted(false);

        // 将组件添加到容器
        categoryContainer.add(categoryComboBox);
        categoryContainer.add(Box.createHorizontalStrut(10));
        categoryContainer.add(addCategoryButton);

        newCategoryPanel.add(newCategoryTextField);
        newCategoryPanel.add(Box.createHorizontalStrut(10));
        newCategoryPanel.add(confirmNewCategoryButton);

        // 添加事件监听
        addCategoryButton.addActionListener(e -> toggleAddCategory());
        confirmNewCategoryButton.addActionListener(e -> addNewCategory());

        fieldPanel.add(fieldLabel);
        fieldPanel.add(Box.createVerticalStrut(8));
        fieldPanel.add(categoryContainer);
        fieldPanel.add(Box.createVerticalStrut(8));
        fieldPanel.add(newCategoryPanel);
        fieldPanel.add(Box.createVerticalStrut(20));

        return fieldPanel;
    }

    /**
     * 切换添加分类模式
     */
    private void toggleAddCategory() {
        if (newCategoryPanel.isVisible()) {
            // 隐藏添加面板，恢复加号按钮
            newCategoryPanel.setVisible(false);
            addCategoryButton.setText("+");
            addCategoryButton.setBackground(new Color(66, 133, 244));
            newCategoryTextField.setText("");
        } else {
            // 显示添加面板，切换为红色X按钮
            newCategoryPanel.setVisible(true);
            addCategoryButton.setText("×");
            addCategoryButton.setBackground(new Color(244, 67, 54));
            newCategoryTextField.requestFocus();
        }
    }

    /**
     * 添加新分类
     */
    private void addNewCategory() {
        String newCategory = newCategoryTextField.getText().trim();
        if (newCategory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "分类名称不能为空", "提示", JOptionPane.INFORMATION_MESSAGE);
            newCategoryTextField.requestFocus();
            return;
        }

        // 检查分类是否已存在
        boolean exists = false;
        for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
            if (categoryComboBox.getItemAt(i).equals(newCategory)) {
                exists = true;
                break;
            }
        }

        if (exists) {
            JOptionPane.showMessageDialog(this, "该分类已存在", "提示", JOptionPane.INFORMATION_MESSAGE);
            newCategoryTextField.requestFocus();
            return;
        }

        // 添加到分类数据核心
        ArticleDataCore.categoriesDataCore.add(newCategory);
        
        // 添加到选择框并选中
        categoryComboBox.addItem(newCategory);
        categoryComboBox.setSelectedItem(newCategory);

        // 恢复到正常模式
        toggleAddCategory();
    }

    /**
     * 创建标签管理面板
     */
    private JPanel createTagManagementPanel() {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setAlignmentX(LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)); // 移除高度限制，允许动态调整

        JLabel fieldLabel = new JLabel("标签");
        fieldLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        fieldLabel.setForeground(new Color(60, 60, 60));
        fieldLabel.setAlignmentX(LEFT_ALIGNMENT);

        // 初始化标签列表
        tagsList = new java.util.ArrayList<>();

        // 标签输入区域（输入框 + 添加按钮）
        JPanel tagInputPanel = new JPanel();
        tagInputPanel.setLayout(new BoxLayout(tagInputPanel, BoxLayout.X_AXIS));
        tagInputPanel.setBackground(Color.WHITE);
        tagInputPanel.setAlignmentX(LEFT_ALIGNMENT);

        tagInputField = new JTextField();
        tagInputField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        tagInputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        tagInputField.setPreferredSize(new Dimension(0, FIELD_HEIGHT));
        tagInputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT));
        tagInputField.setAlignmentX(LEFT_ALIGNMENT);
        // 回车键添加标签
        tagInputField.addActionListener(e -> addTag());

        JButton addTagButton = new JButton("+");
        addTagButton.setPreferredSize(new Dimension(FIELD_HEIGHT, FIELD_HEIGHT));
        addTagButton.setMaximumSize(new Dimension(FIELD_HEIGHT, FIELD_HEIGHT));
        addTagButton.setFont(new Font("微软雅黑", Font.BOLD, 18));
        addTagButton.setBackground(new Color(66, 133, 244));
        addTagButton.setForeground(Color.WHITE);
        addTagButton.setBorder(BorderFactory.createEmptyBorder());
        addTagButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addTagButton.addActionListener(e -> addTag());
        // 按钮悬停效果
        addTagButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addTagButton.setBackground(new Color(52, 108, 204));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addTagButton.setBackground(new Color(66, 133, 244));
            }
        });

        tagInputPanel.add(tagInputField);
        tagInputPanel.add(Box.createHorizontalStrut(10));
        tagInputPanel.add(addTagButton);

        // 标签展示区域
        tagDisplayPanel = new JPanel();
        tagDisplayPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 5, 5));
        tagDisplayPanel.setBackground(Color.WHITE);
        // 移除边框，与背景融为一体
        tagDisplayPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        // 移除固定尺寸限制，让面板能够根据内容动态调整
        tagDisplayPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        tagDisplayPanel.setAlignmentX(LEFT_ALIGNMENT);

        fieldPanel.add(fieldLabel);
        fieldPanel.add(Box.createVerticalStrut(8));
        fieldPanel.add(tagInputPanel);
        fieldPanel.add(Box.createVerticalStrut(8));
        fieldPanel.add(tagDisplayPanel);
        fieldPanel.add(Box.createVerticalStrut(20));

        return fieldPanel;
    }

    /**
     * 添加标签
     */
    private void addTag() {
        String tagText = tagInputField.getText().trim();
        if (!tagText.isEmpty() && !tagsList.contains(tagText)) {
            tagsList.add(tagText);
            addTagToDisplay(tagText);
            tagInputField.setText("");
            tagInputField.requestFocus();
        }
    }

    /**
     * 将标签添加到展示面板
     */
    private void addTagToDisplay(String tagText) {
        // 创建标签组件
        JPanel tagComponent = new JPanel();
        tagComponent.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
        tagComponent.setBackground(new Color(220, 230, 250));
        tagComponent.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        tagComponent.setAlignmentX(LEFT_ALIGNMENT);

        // 标签文本
        JLabel tagLabel = new JLabel(tagText);
        tagLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        tagLabel.setForeground(new Color(60, 60, 60));

        // 删除按钮
        JButton deleteButton = new JButton("×");
        deleteButton.setPreferredSize(new Dimension(16, 16));
        deleteButton.setMaximumSize(new Dimension(16, 16));
        deleteButton.setFont(new Font("微软雅黑", Font.BOLD, 10));
        deleteButton.setBackground(new Color(200, 210, 240));
        deleteButton.setForeground(new Color(100, 100, 100));
        deleteButton.setBorder(BorderFactory.createEmptyBorder());
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.setFocusPainted(false);
        // 删除按钮点击事件
        deleteButton.addActionListener(e -> {
            tagDisplayPanel.remove(tagComponent);
            tagsList.remove(tagText);
            tagDisplayPanel.revalidate();
            tagDisplayPanel.repaint();
        });
        // 删除按钮悬停效果
        deleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                deleteButton.setBackground(new Color(180, 190, 230));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                deleteButton.setBackground(new Color(200, 210, 240));
            }
        });

        tagComponent.add(tagLabel);
        tagComponent.add(Box.createHorizontalStrut(3));
        tagComponent.add(deleteButton);

        tagDisplayPanel.add(tagComponent);
        tagDisplayPanel.revalidate();
        tagDisplayPanel.repaint();
    }

    /**
     * 更新预览内容
     */
    private void updatePreview() {
        if (previewToggle.isSelected()) {
            String markdown = textArea.getText();
            String html = ArticleUtil.markdownToHtml(markdown);
            // 添加基本样式
            String styledHtml = "<html><head><style>"
                    + "body { font-family: 'Microsoft YaHei', sans-serif; font-size: 14px; line-height: 1.6; padding: 0; margin: 0; }"
                    + "h1, h2, h3, h4, h5, h6 { color: #333; margin-top: 20px; margin-bottom: 10px; }"
                    + "h1 { border-bottom: 1px solid #eee; padding-bottom: 5px; }"
                    + "p { margin-bottom: 15px; }"
                    + "ul, ol { margin-left: 20px; margin-bottom: 15px; }"
                    + "li { margin-bottom: 5px; }"
                    + "pre { background-color: #f5f5f5; border: 1px solid #ddd; border-radius: 3px; padding: 10px; overflow-x: auto; font-family: Consolas, monospace; }"
                    + "code { background-color: #f5f5f5; border: 1px solid #ddd; border-radius: 3px; padding: 2px 4px; font-family: Consolas, monospace; font-size: 12px; }"
                    + "blockquote { border-left: 3px solid #ddd; margin: 0; padding-left: 15px; color: #666; }"
                    + "hr { border: none; border-top: 1px solid #eee; margin: 20px 0; }"
                    + "a { color: #428bca; text-decoration: none; }"
                    + "a:hover { text-decoration: underline; }"
                    + "img { max-width: 100%; height: auto; }"
                    + "</style></head><body>" + html + "</body></html>";
            
            previewPane.setText(styledHtml);
            previewPane.setCaretPosition(0); // 滚动到顶部
        }
    }

    /**
     * 自定义换行布局管理器
     */
    private static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();

                if (targetWidth == 0 && preferred) {
                    targetWidth = Integer.MAX_VALUE;
                }

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
                int maxWidth = targetWidth - horizontalInsetsAndGap;

                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                int nmembers = target.getComponentCount();

                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                        if (rowWidth + d.width > maxWidth) {
                            addRow(dim, rowWidth, rowHeight);
                            rowWidth = 0;
                            rowHeight = 0;
                        }

                        if (rowWidth != 0) {
                            rowWidth += hgap;
                        }

                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }

                addRow(dim, rowWidth, rowHeight);

                dim.width += horizontalInsetsAndGap;
                dim.height += insets.top + insets.bottom + vgap * 2;

                return dim;
            }
        }

        private void addRow(Dimension dim, int rowWidth, int rowHeight) {
            dim.width = Math.max(dim.width, rowWidth);
            if (dim.height > 0) {
                dim.height += getVgap();
            }
            dim.height += rowHeight;
        }
    }

    // ================= 通用表单字段 =================

    private JPanel createFormField(String label, JComponent component) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setAlignmentX(LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT + 40));

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        fieldLabel.setForeground(new Color(60, 60, 60));
        fieldLabel.setAlignmentX(LEFT_ALIGNMENT);

        component.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        component.setPreferredSize(new Dimension(0, FIELD_HEIGHT));
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT));
        component.setAlignmentX(LEFT_ALIGNMENT);

        fieldPanel.add(fieldLabel);
        fieldPanel.add(Box.createVerticalStrut(8));
        fieldPanel.add(component);
        fieldPanel.add(Box.createVerticalStrut(20));

        return fieldPanel;
    }

    // ================= 保存按钮 =================

    private JButton createSaveButton() {
        saveButton = new JButton("保存文章");
        saveButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        saveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT));
        saveButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        saveButton.setBackground(new Color(66, 133, 244));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(BorderFactory.createEmptyBorder());
        saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return saveButton;
    }
}
