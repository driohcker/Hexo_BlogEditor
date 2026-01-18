package org.sxi.ui;

import org.sxi.util.ArticleUtil;
import org.sxi.vo.Article;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * 文章展示页面Fragment，用于显示完整的文章内容
 */
public class ArticleViewFragment extends JDialog {
    // UI常量
    private static final int WINDOW_WIDTH = 1400;
    private static final int WINDOW_HEIGHT = 1000;
    private static final int CARD_PADDING = 30;
    
    // 文章对象
    private Article article;
    
    /**
     * 构造方法
     * @param parent 父窗口
     * @param article 文章对象
     */
    public ArticleViewFragment(Frame parent, Article article) {
        super(parent, "文章详情", true);
        this.article = article;
        initComponents();
        initEvents();
    }
    
    /**
     * 初始化组件
     */
    private void initComponents() {
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // 创建中央内容卡片
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(CARD_PADDING, CARD_PADDING, CARD_PADDING, CARD_PADDING)
        ));
        
        // 文章标题
        JLabel titleLabel = new JLabel(article.getTitle());
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(30, 30, 30));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        // 文章元信息面板
        JPanel metaPanel = new JPanel();
        metaPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        metaPanel.setBackground(Color.WHITE);
        metaPanel.setAlignmentX(LEFT_ALIGNMENT);
        
        // 日期
        JLabel dateLabel = new JLabel("日期: " + article.getDate().toString());
        dateLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(100, 100, 100));
        
        // 分类
        JLabel categoriesLabel = new JLabel("分类: " + listToString(article.getCategories()));
        categoriesLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        categoriesLabel.setForeground(new Color(100, 100, 100));
        
        // 标签
        JLabel tagsLabel = new JLabel("标签: " + listToString(article.getTags()));
        tagsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        tagsLabel.setForeground(new Color(100, 100, 100));
        
        metaPanel.add(dateLabel);
        metaPanel.add(categoriesLabel);
        metaPanel.add(tagsLabel);
        
        // 分隔线
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        separator.setAlignmentX(LEFT_ALIGNMENT);
        
        // 文章内容（使用JEditorPane显示HTML）
        JEditorPane contentArea = new JEditorPane();
        contentArea.setContentType("text/html");
        contentArea.setEditable(false);
        contentArea.setOpaque(true);
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder());
        
        // 设置HTML样式
        String htmlStyle = "<style>"
                + "body { font-family: '微软雅黑', Arial, sans-serif; font-size: 14px; line-height: 1.6; color: #333; }"
                + "h1, h2, h3, h4, h5, h6 { color: #2c3e50; margin-top: 24px; margin-bottom: 16px; }"
                + "h1 { font-size: 28px; border-bottom: 1px solid #eee; padding-bottom: 8px; }"
                + "h2 { font-size: 24px; }"
                + "h3 { font-size: 20px; }"
                + "p { margin-bottom: 16px; }"
                + "ul, ol { margin-bottom: 16px; padding-left: 24px; }"
                + "li { margin-bottom: 8px; }"
                + "blockquote { border-left: 4px solid #ddd; padding-left: 16px; color: #666; margin: 16px 0; }"
                + "code { background-color: #f5f5f5; padding: 2px 4px; border-radius: 3px; font-family: Consolas, monospace; }"
                + "pre { background-color: #f5f5f5; padding: 16px; border-radius: 4px; overflow-x: auto; }"
                + "a { color: #3498db; text-decoration: none; }"
                + "a:hover { text-decoration: underline; }"
                + "img { max-width: 100%; height: auto; }"
                + "</style>";
        
        // 加载文章内容
        try {
            String markdownContent = ArticleUtil.getArticleContent(article);
            String htmlContent = ArticleUtil.markdownToHtml(markdownContent);
            // 将HTML内容和样式组合
            String fullHtml = "<!DOCTYPE html><html><head>" + htmlStyle + "</head><body>" + htmlContent + "</body></html>";
            contentArea.setText(fullHtml);
            // 滚动到顶部
            contentArea.setCaretPosition(0);
        } catch (IOException e) {
            contentArea.setText("加载文章内容失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 内容滚动面板
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentScrollPane.setPreferredSize(new Dimension(0, 800));
        contentScrollPane.setAlignmentX(LEFT_ALIGNMENT);
        
        // 添加组件到卡片面板
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(15));
        cardPanel.add(metaPanel);
        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(separator);
        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(contentScrollPane);
        
        // 添加卡片面板到窗口
        add(cardPanel, BorderLayout.CENTER);
    }
    
    /**
     * 初始化事件
     */
    private void initEvents() {
        // 窗口关闭事件已由JDialog默认处理
    }
    
    /**
     * 将列表转换为字符串
     * @param list 列表
     * @return 字符串表示
     */
    private String listToString(java.util.List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.join(", ", list);
    }
}