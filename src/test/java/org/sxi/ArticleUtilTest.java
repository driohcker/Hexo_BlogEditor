package org.sxi;

import org.junit.jupiter.api.Test;
import org.sxi.util.ArticleUtil;
import org.sxi.vo.Article;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * ArticleUtil工具类测试
 */

public class ArticleUtilTest {

    @Test
    public void test01(){
        System.out.println("=== ArticleUtil 工具类测试 ===");

        try {
            // 测试文件路径
            File file = new File("src/main/resources/日常记录.md");

            // 读取文章
            Article article = ArticleUtil.readArticleFromFile(file);

            // 打印文章信息
            System.out.println("文章标题: " + article.getTitle());
            System.out.println("文章日期: " + article.getDate());
            System.out.println("文章分类: " + listToString(article.getCategories()));
            System.out.println("文章标签: " + listToString(article.getTags()));
            System.out.println("文章名: " + article.getFileName());
            System.out.println("\n文章内容预览: " + article.getContent().substring(0, Math.min(100, article.getContent().length())) + "...");

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
    
    /**
     * 将列表转换为字符串
     * @param list 列表
     * @return 字符串表示
     */
    private static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        return "[" + String.join(", ", list) + "]";
    }
}