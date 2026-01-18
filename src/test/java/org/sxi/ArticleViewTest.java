package org.sxi;

import org.sxi.ui.ArticleViewFragment;
import org.sxi.util.ArticleUtil;
import org.sxi.vo.Article;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * 文章查看功能测试
 */
public class ArticleViewTest {
    public static void main(String[] args) {
        System.out.println("=== 文章查看功能测试 ===");
        
        try {
            // 测试文件路径
            File file = new File("src/main/resources/日常记录.md");
            
            // 读取文章信息
            System.out.println("1. 读取文章信息...");
            Article article = ArticleUtil.readArticleFromFile(file);
            System.out.println("文章标题: " + article.getTitle());
            
            // 测试获取文章内容
            System.out.println("\n2. 获取文章内容...");
            String content = ArticleUtil.getArticleContent(article);
            System.out.println("文章内容长度: " + content.length() + " 字符");
            System.out.println("内容预览: " + content.substring(0, Math.min(100, content.length())) + "...");
            
            // 测试打开文章查看窗口
            System.out.println("\n3. 打开文章查看窗口...");
            SwingUtilities.invokeLater(() -> {
                ArticleViewFragment viewFragment = new ArticleViewFragment(null, article);
                viewFragment.setVisible(true);
            });
            
            System.out.println("\n=== 测试完成 ===");
        } catch (IOException e) {
            System.err.println("文件读取失败: " + e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            System.err.println("日期解析失败: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}