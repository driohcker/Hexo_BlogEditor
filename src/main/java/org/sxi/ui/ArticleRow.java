package org.sxi.ui;

import org.sxi.biz.ArticleBiz;
import org.sxi.vo.Article;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

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
    private String date;

    private AbstractButton viewBtn,editBtn,deleteBtn;

    public ArticleRow(Article article){
        this.article = article;

        this.title = article.getTitle();
        // 处理空分类情况
        this.category = article.getCategories() != null && !article.getCategories().isEmpty() ?
                article.getCategories().get(0) : "未分类";
        // 处理空标签情况
        this.tags = article.getTags() != null && !article.getTags().isEmpty() ?
                String.join(", ", article.getTags()) : "无标签";
        // 处理空日期情况
        this.date = article.getDate() != null ?
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(article.getDate()) : "无日期";
        initComponents();
        initStyles();
        initEvents();
    }

    /**
     * 初始化组件（现代风格布局）
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(0, 90)); // 增加高度以容纳更多内容
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 232, 236)),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        /* ================= 左侧信息区 ================= */

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // 标题（强调）
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setForeground(new Color(30, 41, 59));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        // 元信息面板（分类 + 标签 + 日期）
        JPanel metaPanel = new JPanel();
        metaPanel.setLayout(new BoxLayout(metaPanel, BoxLayout.X_AXIS));
        metaPanel.setOpaque(false);
        metaPanel.setAlignmentX(LEFT_ALIGNMENT);

        // 分类区域（固定宽度）
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        categoryPanel.setOpaque(false);
        categoryPanel.setPreferredSize(new Dimension(150, 24));
        categoryPanel.setMaximumSize(new Dimension(150, 24));
        
        JLabel fromLabel = new JLabel("From");
        fromLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        fromLabel.setForeground(new Color(148, 163, 184));
        
        JPanel categoryBadge = new JPanel();
        categoryBadge.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
        categoryBadge.setBackground(new Color(241, 245, 249));
        categoryBadge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        
        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        categoryLabel.setForeground(new Color(59, 130, 246));
        
        categoryBadge.add(categoryLabel);
        categoryPanel.add(fromLabel);
        categoryPanel.add(Box.createHorizontalStrut(4));
        categoryPanel.add(categoryBadge);

        // 标签区域（固定宽度）
        JPanel tagsPanel = new JPanel();
        tagsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tagsPanel.setOpaque(false);
        tagsPanel.setPreferredSize(new Dimension(400, 24));
        tagsPanel.setMaximumSize(new Dimension(400, 24));
        
        if (!tags.equals("无标签")) {
            String[] tagArray = tags.split(", ");
            for (String tag : tagArray) {
                JPanel tagBadge = new JPanel();
                tagBadge.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
                tagBadge.setBackground(getTagColor(tag)); // 为每个标签生成不同颜色
                tagBadge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                
                JLabel tagLabel = new JLabel(tag);
                tagLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
                tagLabel.setForeground(Color.WHITE);
                
                tagBadge.add(tagLabel);
                tagsPanel.add(tagBadge);
                tagsPanel.add(Box.createHorizontalStrut(6));
            }
        } else {
            JLabel noTagsLabel = new JLabel("无标签");
            noTagsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
            noTagsLabel.setForeground(new Color(148, 163, 184));
            tagsPanel.add(noTagsLabel);
        }

        // 日期区域（固定宽度，居右）
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        datePanel.setOpaque(false);
        datePanel.setPreferredSize(new Dimension(150, 24));
        datePanel.setMaximumSize(new Dimension(150, 24));
        
        JLabel dateLabel = new JLabel(getFormattedDate());
        dateLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(148, 163, 184));
        datePanel.add(dateLabel);

        // 添加元信息组件
        metaPanel.add(categoryPanel);
        metaPanel.add(Box.createHorizontalStrut(10));
        metaPanel.add(tagsPanel);
        metaPanel.add(Box.createHorizontalGlue());
        metaPanel.add(datePanel);

        // 添加到信息面板
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(12));
        infoPanel.add(metaPanel);

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
     * 根据标签生成颜色（固定算法，确保同一标签始终为同一颜色）
     */
    private Color getTagColor(String tag) {
        // 预设的鲜艳颜色
        Color[] colors = {
            new Color(239, 83, 80), // 红色
            new Color(255, 112, 67), // 橙色
            new Color(255, 193, 7),  // 黄色
            new Color(76, 175, 80),  // 绿色
            new Color(25, 118, 210), // 蓝色
            new Color(103, 58, 183), // 紫色
            new Color(233, 30, 99),  // 粉色
            new Color(0, 188, 212)   // 青色
        };
        
        // 根据标签的哈希码选择颜色，确保同一标签始终为同一颜色
        int hash = tag.hashCode();
        int index = Math.abs(hash) % colors.length;
        return colors[index];
    }
    
    /**
     * 格式化日期，简化显示
     */
    private String getFormattedDate() {
        if (article.getDate() == null) {
            return "无日期";
        }
        
        Date now = new Date();
        Calendar articleCal = Calendar.getInstance();
        articleCal.setTime(article.getDate());
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(now);
        
        int articleYear = articleCal.get(Calendar.YEAR);
        int nowYear = nowCal.get(Calendar.YEAR);
        int articleMonth = articleCal.get(Calendar.MONTH) + 1;
        int nowMonth = nowCal.get(Calendar.MONTH) + 1;
        int articleDay = articleCal.get(Calendar.DAY_OF_MONTH);
        int nowDay = nowCal.get(Calendar.DAY_OF_MONTH);
        
        // 如果是今天
        if (articleYear == nowYear && articleMonth == nowMonth && articleDay == nowDay) {
            return new java.text.SimpleDateFormat("HH:mm").format(article.getDate());
        }
        // 如果是今年
        else if (articleYear == nowYear) {
            return new java.text.SimpleDateFormat("MM月dd日 HH:mm").format(article.getDate());
        }
        // 其他情况
        else {
            return new java.text.SimpleDateFormat("yyyy年MM月dd日").format(article.getDate());
        }
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
