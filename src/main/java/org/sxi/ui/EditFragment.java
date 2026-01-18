package org.sxi.ui;

import org.sxi.biz.ArticleBiz;
import org.sxi.biz.BizException;
import org.sxi.dao.ArticleDataCore;
import org.sxi.util.ArticleUtil;
import org.sxi.vo.Article;
import org.sxi.vo.Result;

import javax.swing.*;
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
    JComboBox<String> categoryComboBox;
    JTextField tagTextField;

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
            article.setCategories(Collections.singletonList(categoryComboBox.getSelectedItem().toString()));
            article.setTags(Collections.singletonList(tagTextField.getText()));
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

    public Result setData(Article article, boolean isNew){
        this.isNew = isNew;

        if (article == null) {
            throw new BizException("文章不能为空");
        }
        this.article = article;

        titleTextField.setText(article.getTitle());

        categoryComboBox.setSelectedItem(article.getCategories());

        tagTextField.setText(article.getTags().toString().substring(1, article.getTags().toString().length() - 1));

        try {
            textArea.setText(ArticleUtil.getArticleContent(article));
        } catch (IOException e) {
            throw new BizException("读取文章内容失败");
        }

        return Result.ok("初始化数据成功");
    }

    // ================= 左侧：元信息 =================

    private JPanel createLeftMetaPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, 0));
        panel.setMaximumSize(new Dimension(LEFT_PANEL_WIDTH, Integer.MAX_VALUE));
        panel.setAlignmentX(LEFT_ALIGNMENT);

        titleTextField = new JTextField();
        categoryComboBox = new JComboBox<>();
        tagTextField = new JTextField();

        panel.add(createFormField("标题", titleTextField));
        panel.add(createFormField("分类", categoryComboBox));
        panel.add(createFormField("标签", tagTextField));

        panel.add(Box.createVerticalGlue());

        JButton saveButton = createSaveButton();
        saveButton.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(saveButton);

        return panel;
    }

    // ================= 右侧：正文编辑 =================

    private JPanel createContentEditorPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel editorLabel = new JLabel("内容");
        editorLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        editorLabel.setForeground(new Color(60, 60, 60));
        editorLabel.setAlignmentX(LEFT_ALIGNMENT);

        textArea = new JTextArea();
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(0, 500));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);

        JLabel markdownHint = new JLabel("支持 Markdown 语法");
        markdownHint.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        markdownHint.setForeground(new Color(120, 120, 120));
        markdownHint.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(editorLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(8));
        panel.add(markdownHint);

        return panel;
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
