package org.sxi.util;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.sxi.dao.PropertiesDataCore;
import org.sxi.vo.Article;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文章工具类，用于从markdown文件中读取文章属性和内容
 */
public class ArticleUtil {
    // 日期格式
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static String file_root_path = PropertiesDataCore.getStringProperty("post.root.path");
    
    /**
     * 从文件中读取文章信息（不包含内容）
     * @param file markdown文件
     * @return Article对象
     * @throws IOException 如果文件读取失败
     * @throws ParseException 如果日期格式解析失败
     */
    public static Article readArticleFromFile(File file) throws IOException, ParseException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("无效的文件对象: " + file.getName());
        }
        
        Article article = new Article();

        article.setFileName(file.getName().substring(0, file.getName().lastIndexOf(".")));
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            boolean inFrontMatter = false;
            StringBuilder frontMatter = new StringBuilder();
            
            // 读取文件内容
            while ((line = reader.readLine()) != null) {
                if (line.equals("---")) {
                    if (!inFrontMatter) {
                        inFrontMatter = true;
                    } else {
                        inFrontMatter = false;
                        // 解析前导属性
                        parseFrontMatter(frontMatter.toString(), article);
                        break; // 只解析前导属性，不读取内容
                    }
                } else if (inFrontMatter) {
                    frontMatter.append(line).append("\n");
                }
            }
        }
        
        return article;
    }
    
    /**
     * 根据文章标题获取文章内容
     * @param article 文章对象
     * @return 文章内容（去除前导属性）
     * @throws IOException 如果文件读取失败
     */
    public static String getArticleContent(Article article) throws IOException {
        if (article == null) {
            throw new IllegalArgumentException("无效的文章对象");
        }

        file_root_path = PropertiesDataCore.getStringProperty("post.root.path");

        File file = new File(file_root_path + article.getFileName() + ".md");
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在: " + file.getAbsolutePath());
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            boolean inFrontMatter = false;
            boolean contentStarted = false;
            StringBuilder content = new StringBuilder();
            
            // 读取文件内容，跳过前导属性
            while ((line = reader.readLine()) != null) {
                if (line.equals("---")) {
                    if (!inFrontMatter) {
                        inFrontMatter = true;
                    } else {
                        inFrontMatter = false;
                        contentStarted = true;
                        continue;
                    }
                } else if (contentStarted) {
                    content.append(line).append("\n");
                }
            }
            
            return content.toString().trim();
        }
    }
    
    /**
     * 解析YAML前导属性
     * @param frontMatter 前导属性字符串
     * @param article Article对象
     * @throws ParseException 如果日期格式解析失败
     */
    private static void parseFrontMatter(String frontMatter, Article article) throws ParseException {
        // 解析标题
        Pattern titlePattern = Pattern.compile("^title:\\s*(.+)$", Pattern.MULTILINE);
        Matcher titleMatcher = titlePattern.matcher(frontMatter);
        if (titleMatcher.find()) {
            article.setTitle(titleMatcher.group(1).trim());
        }
        
        // 解析日期
        Pattern datePattern = Pattern.compile("^date:\s*(.+)$", Pattern.MULTILINE);
        Matcher dateMatcher = datePattern.matcher(frontMatter);
        if (dateMatcher.find()) {
            String dateStr = dateMatcher.group(1).trim();
            Date date = DATE_FORMAT.parse(dateStr);
            article.setDate(date);
        }
        
        // 解析分类
        Pattern categoriesPattern = Pattern.compile(
                "^categories:\\s*\\n((?:\\s*-\\s*.+\\n)*)",
                Pattern.MULTILINE
        );
        Matcher categoriesMatcher = categoriesPattern.matcher(frontMatter);
        if (categoriesMatcher.find()) {
            String block = categoriesMatcher.group(1);
            article.setCategories(parseList(block));
        }


        // 解析标签
        Pattern tagsPattern = Pattern.compile(
                "^tags:\\s*\\n((?:\\s*-\\s*.+\\n)*)",
                Pattern.MULTILINE
        );
        Matcher tagsMatcher = tagsPattern.matcher(frontMatter);
        if (tagsMatcher.find()) {
            String block = tagsMatcher.group(1);
            article.setTags(parseList(block));
        }

    }
    
    /**
     * 解析YAML列表格式
     * @param listBlock 列表字符串
     * @return 字符串列表
     */
    private static List<String> parseList(String listBlock) {
        List<String> result = new ArrayList<>();
        if (listBlock == null || listBlock.isBlank()) {
            return result; // tags: 空 → []
        }

        Pattern listItemPattern =
                Pattern.compile("^\\s*-\\s*(.+)$", Pattern.MULTILINE);
        Matcher matcher = listItemPattern.matcher(listBlock);

        while (matcher.find()) {
            result.add(matcher.group(1).trim());
        }
        return result;
    }
    
    /**
     * 将Markdown内容转换为HTML
     * @param markdown Markdown内容
     * @return HTML内容
     */
    public static String markdownToHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        
        // 创建Markdown解析器
        Parser parser = Parser.builder().build();
        // 解析Markdown内容为AST
        Node document = parser.parse(markdown);
        // 创建HTML渲染器
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        // 将AST渲染为HTML
        return renderer.render(document);
    }
    
    /**
     * 将Article对象保存为markdown文件
     * @param article Article对象
     * @return 保存的文件
     * @throws IOException 如果文件保存失败
     */
    public static File saveArticleToFile(Article article) throws IOException {
        if (article == null || article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("无效的文章对象或标题为空");
        }
        
        // 生成文件名（清理特殊字符）
        String fileName = article.getFileName().trim()
                .replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5_-]", "") + ".md";

        file_root_path = PropertiesDataCore.getStringProperty("post.root.path");

        // 默认保存路径（当前目录）
        File file = new File(file_root_path + fileName);
        System.out.println("设置文件名：" + fileName);
        System.out.println("设置文件路径" + file.getAbsolutePath());
        
        // 构建markdown内容
        StringBuilder markdownContent = new StringBuilder();
        
        // 添加前导属性
        markdownContent.append("---\n");
        markdownContent.append("title: ").append(article.getTitle()).append("\n");
        System.out.println("前导属性-设置标题：" + article.getTitle());

        // 添加日期（如果存在）
        if (article.getDate() != null) {
            markdownContent.append("date: ").append(DATE_FORMAT.format(article.getDate())).append("\n");
            System.out.println("前导属性-设置日期：" + DATE_FORMAT.format(article.getDate()));
        }
        
        // 添加分类（如果存在）
        if (article.getCategories() != null && !article.getCategories().isEmpty()) {
            markdownContent.append("categories:\n");
            for (String category : article.getCategories()) {
                markdownContent.append("    - ").append(category).append("\n");
            }
            System.out.println("前导属性-设置分类：" + article.getCategories());
        }
        
        // 添加标签（如果存在）
        if (article.getTags() != null && !article.getTags().isEmpty()) {
            markdownContent.append("tags:\n");
            for (String tag : article.getTags()) {
                markdownContent.append("    - ").append(tag).append("\n");
            }
            System.out.println("前导属性-设置标签" + article.getTags());
        }
        
        markdownContent.append("---\n\n");
        
        // 添加正文内容（如果存在）
        if (article.getContent() != null && !article.getContent().trim().isEmpty()) {
            markdownContent.append(article.getContent()).append("\n");
            System.out.println("设置正文");
        }
        
        // 写入文件
        try (java.io.PrintWriter writer = new java.io.PrintWriter(file, "UTF-8")) {
            writer.print(markdownContent.toString());
        }
        
        System.out.println("文章已保存到: " + file.getAbsolutePath());
        return file;
    }

}