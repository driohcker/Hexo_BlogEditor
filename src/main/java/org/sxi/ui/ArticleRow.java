package org.sxi.ui;

import org.sxi.biz.ArticleBiz;
import org.sxi.vo.Article;

import javax.swing.*;
import java.awt.*;

/**
 * 文章行组件（UI 美化版）
 */
public class ArticleRow extends JPanel {

    private static final int ROW_HEIGHT = 65;
    private static final int BUTTON_WIDTH = 72;
    private static final int BUTTON_HEIGHT = 28;

    private Article article;

    private ArticleBiz articleBiz = new ArticleBiz();

    private String title;
    private String category;
    private String tags;

    private AbstractButton viewBtn,editBtn,deleteBtn;

    public ArticleRow(Article article){
        this.article = article;

        this.title = article.getTitle();
        // 处理空分类情况
        this.category = article.getCategories() != null && !article.getCategories().isEmpty() ?
                article.getCategories().get(0) : "未分类";
        this.tags = article.getTags() != null && !article.getTags().isEmpty() ?
                article.getTags().get(0) : "无标签";
        initComponents();
        initStyles();
        initEvents();
    }

    /**
     * 初始化组件（同一行布局）
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(0, ROW_HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 232, 236)),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        /* ================= 左侧信息区 ================= */

        JPanel infoPanel = new JPanel(new BorderLayout(20, 0));
        infoPanel.setOpaque(false);

        // 标题（左）
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        titleLabel.setForeground(new Color(30, 41, 59));
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);

        // 分类 + 标签（中）
        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        metaPanel.setOpaque(false);

        JLabel categoryLabel = new JLabel("分类 · " + category);
        categoryLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        categoryLabel.setForeground(new Color(100, 116, 139));

        JLabel tagsLabel = new JLabel("标签 · " + tags);
        tagsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        tagsLabel.setForeground(new Color(59, 130, 246));

        metaPanel.add(categoryLabel);
        metaPanel.add(tagsLabel);

        infoPanel.add(titleLabel, BorderLayout.WEST);
        infoPanel.add(metaPanel, BorderLayout.CENTER);

        /* ================= 右侧按钮区 ================= */

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

         viewBtn = createActionButton("查看");
         editBtn = createActionButton("修改");
         deleteBtn = createActionButton("删除");

        actionPanel.add(viewBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);

        /* ================= 添加到主布局 ================= */

        add(infoPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.EAST);
    }

    /**
     * 行悬停效果
     */
    private void initStyles(){
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setBackground(new Color(248, 250, 252));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setBackground(Color.WHITE);
            }
        });
    }


    private void initEvents() {
        viewBtn.addActionListener(e -> {
            if (article != null) {
                // 打开文章查看窗口
                ArticleViewFragment viewFragment = new ArticleViewFragment(
                        (Frame) SwingUtilities.getWindowAncestor(this),
                        article
                );
                viewFragment.setVisible(true);
            } else {
                // 对于字符串构造的ArticleRow，显示简单提示
                JOptionPane.showMessageDialog(this, "查看文章: " + title);
            }
        });

        editBtn.addActionListener(e ->{
            //JOptionPane.showMessageDialog(this, "修改文章: " + title)
            EditFragment info_editFragment = new EditFragment();
            info_editFragment.setData(article);
            MainActivity.getInstance().showFragment(info_editFragment);

        });

        deleteBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "确定要删除文章: " + title + " 吗?",
                    "删除确认",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                //JOptionPane.showMessageDialog(this, "删除文章: " + title);
                articleBiz.deleteArticle(article);
                BrowseFragment.getBrowseFragmentInstance().refreshData();
            }
        });
    }
    
    /**
     * 将列表转换为字符串格式
     * @param list 列表
     * @return 格式化后的字符串
     */
    private String listToString(java.util.List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.join(", ", list);
    }

    /**
     * 创建操作按钮（克制风格）
     */
    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if ("查看".equals(text)) {
            button.setBackground(new Color(241, 245, 249));
            button.setForeground(new Color(51, 65, 85));
        } else if ("修改".equals(text)) {
            button.setBackground(new Color(59, 130, 246));
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(new Color(239, 68, 68));
            button.setForeground(Color.WHITE);
        }

        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(button.getBackground().darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if ("查看".equals(text)) {
                    button.setBackground(new Color(241, 245, 249));
                } else if ("修改".equals(text)) {
                    button.setBackground(new Color(59, 130, 246));
                } else {
                    button.setBackground(new Color(239, 68, 68));
                }
            }
        });

        return button;
    }
}
