package org.sxi;

import org.sxi.util.ArticleUtil;
import org.sxi.vo.Article;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 文章保存功能测试
 */
public class ArticleSaveTest {
    public static void main(String[] args) {
        System.out.println("=== 文章保存功能测试 ===");
        
        try {
            // 创建测试文章
            Article article = new Article();
            article.setTitle("测试文章保存功能");
            article.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2026-01-18 14:30:00"));
            article.setCategories(Arrays.asList("测试", "技术"));
            article.setTags(Arrays.asList("Java", "测试", "保存"));
            article.setContent("# 测试文章\n\n这是一篇用于测试文章保存功能的示例文章。\n\n## 章节一\n\n- 列表项1\n- 列表项2\n- 列表项3\n\n## 章节二\n\n**粗体文本** 和 *斜体文本* 测试。\n\n[链接测试](https://www.example.com)");
            
            // 保存文章到文件
            System.out.println("1. 保存文章...");
            File savedFile = ArticleUtil.saveArticleToFile(article);
            System.out.println("文章已保存到: " + savedFile.getAbsolutePath());
            System.out.println("文件大小: " + savedFile.length() + " 字节");
            
            // 验证文件是否存在
            if (savedFile.exists()) {
                System.out.println("\n2. 文件保存成功，开始验证内容...");
                
                // 读取保存的文章
                Article readArticle = ArticleUtil.readArticleFromFile(savedFile);
                System.out.println("读取到的文章标题: " + readArticle.getTitle());
                System.out.println("读取到的文章分类: " + readArticle.getCategories());
                System.out.println("读取到的文章标签: " + readArticle.getTags());
                
                // 获取文章内容
                String content = ArticleUtil.getArticleContent(readArticle);
                System.out.println("文章内容长度: " + content.length() + " 字符");
                System.out.println("内容预览: " + content.substring(0, Math.min(100, content.length())) + "...");
                
                // 测试删除临时文件
                System.out.println("\n3. 测试删除临时文件...");
                if (savedFile.delete()) {
                    System.out.println("临时文件已删除");
                } else {
                    System.out.println("临时文件删除失败");
                }
            } else {
                System.err.println("文件保存失败");
            }
            
            System.out.println("\n=== 测试完成 ===");
        } catch (IOException e) {
            System.err.println("文件操作失败: " + e.getMessage());
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