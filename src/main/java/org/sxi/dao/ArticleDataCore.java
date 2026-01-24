package org.sxi.dao;

import lombok.Getter;
import org.sxi.biz.BizException;
import org.sxi.util.ArticleUtil;
import org.sxi.util.FileUtil;
import org.sxi.vo.Article;
import org.sxi.vo.Result;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class ArticleDataCore {

    @Getter
    public static Map<String, Object> articleDataCore = new LinkedHashMap<>();

    @Getter
    public static List<String> categoriesDataCore = new ArrayList<>();

    @Getter
    public static List<String> tagsDataCore = new ArrayList<>();

    // 分类索引
    private static Map<String, List<Article>> categoryIndex = new HashMap<>();
    
    // 标签索引
    private static Map<String, List<Article>> tagIndex = new HashMap<>();

    private static String file_root_path = PropertiesDataCore.getStringProperty("post.root.path") + File.separator;

    static String[] files;

    static {
        files = FileUtil.listFiles(file_root_path);
        initArtcleData();
        initcategoriesData();
    }

    private static Result initArtcleData() {
        // 开始计时
        long startTime = System.currentTimeMillis();
        
        // 如果没有文件，返回成功结果，不抛出异常
        if (files.length == 0) {
            // 结束计时
            long endTime = System.currentTimeMillis();
            long loadTime = endTime - startTime;
            System.out.println("暂无文章，加载时间: " + loadTime + "ms");
            return Result.ok("暂无文章", null);
        }

        // 先将所有文章读取到临时列表中
        List<Article> articles = new ArrayList<>();
        Map<String, Article> fileNameToArticle = new HashMap<>();

        for (String file : files) {
            try {
                Article article = ArticleUtil.readArticleFromFile(new File(file_root_path + file));
                articles.add(article);
                fileNameToArticle.put(file.substring(0, file.lastIndexOf(".")), article);
            } catch (IOException | ParseException e) {
                System.err.println("加载文章失败: " + file + ", 错误信息: " + e.getMessage());
                // 不抛出异常，继续处理其他文件
            }
        }

        // 对文章按日期降序排序（最新的在前）
        articles.sort((a1, a2) -> {
            if (a1.getDate() == null && a2.getDate() == null) return 0;
            if (a1.getDate() == null) return 1;
            if (a2.getDate() == null) return -1;
            return a2.getDate().compareTo(a1.getDate());
        });

        // 清空旧数据并按排序后的顺序添加
        articleDataCore.clear();
        for (Article article : articles) {
            // 找到对应的文件名
            for (Map.Entry<String, Article> entry : fileNameToArticle.entrySet()) {
                if (entry.getValue() == article) {
                    articleDataCore.put(entry.getKey(), article);
                    break;
                }
            }
        }
        
        // 结束计时
        long endTime = System.currentTimeMillis();
        long loadTime = endTime - startTime;
        
        System.out.println("成功加载 " + articleDataCore.size() + " 篇文章，加载时间: " + loadTime + "ms");
//        articleDataCore.forEach((k, v) -> {
//            System.out.println(k + "   " + v);
//        });
        
        return Result.ok("加载文章数据成功", null);
    }

    private static Result initcategoriesData() {

        // 如果没有文件，返回成功结果，不抛出异常
        if (files.length == 0) {
            System.out.println("暂无文章，无法加载分类和标签数据");
            return Result.ok("暂无文章，无法加载分类和标签数据");
        }

        // 清空旧数据
        categoriesDataCore.clear();
        tagsDataCore.clear();
        categoryIndex.clear();
        tagIndex.clear();

        Arrays.stream(files).forEach(file -> {
            try {
                Article article = ArticleUtil.readArticleFromFile(new File(file_root_path + file));
                
                // 处理分类
                if (article.getCategories() != null && !article.getCategories().isEmpty()) {
                    String category = article.getCategories().get(0);
                    if (!categoriesDataCore.contains(category)) {
                        categoriesDataCore.add(category);
                    }
                    // 更新分类索引
                    if (!categoryIndex.containsKey(category)) {
                        categoryIndex.put(category, new ArrayList<>());
                    }
                    categoryIndex.get(category).add(article);
                }
                
                // 处理标签
                if (article.getTags() != null && !article.getTags().isEmpty()) {
                    for (String tag : article.getTags()) {
                        if (!tagsDataCore.contains(tag)) {
                            tagsDataCore.add(tag);
                        }
                        // 更新标签索引
                        if (!tagIndex.containsKey(tag)) {
                            tagIndex.put(tag, new ArrayList<>());
                        }
                        tagIndex.get(tag).add(article);
                    }
                }
            } catch (IOException | ParseException | IndexOutOfBoundsException e) {
                System.err.println("加载文章分类和标签失败: " + file + ", 错误信息: " + e.getMessage());
                // 不抛出异常，继续处理其他文件
            }
        });

        System.out.println("成功加载 " + categoriesDataCore.size() + " 个分类和 " + tagsDataCore.size() + " 个标签");
        return Result.ok("加载分类和标签数据成功");
    }

    public static Result reInitArtcleData() {
        // 重新获取最新的配置路径
        file_root_path = PropertiesDataCore.getStringProperty("post.root.path");
        // 清空旧数据
        articleDataCore.clear();

        files = FileUtil.listFiles(file_root_path);

        // 重新初始化数据
        return initArtcleData();
    }


    public static Result reInitcategoriesData() {
        // 重新获取最新的配置路径
        file_root_path = PropertiesDataCore.getStringProperty("post.root.path");
        // 清空旧数据
        categoriesDataCore.clear();
        tagsDataCore.clear();
        categoryIndex.clear();
        tagIndex.clear();

        //TODO：暂时先用全局刷新代替动态刷新，不然会引起读空数据
        // 动态刷新性能较优，需要判空分类存储
        files = FileUtil.listFiles(file_root_path);

        // 重新初始化数据
        return initcategoriesData();
    }

    public static Article getArticle(String fileName){
        return (Article) articleDataCore.get(fileName);
    }

    public static Result addArticle(Article article){
        try {
            String filename_md = ArticleUtil.saveArticleToFile(article).getName();
            articleDataCore.put(filename_md.substring(0, filename_md.lastIndexOf(".")), article);
        } catch (IOException e) {
            throw new BizException("新建文件错误");
        }
        return Result.ok("新建文件成功");
    }

    /**
     * 编辑文章
     * @param article 文章对象
     * @return Result对象
     */
    public static Result editArticle(Article article){
        try {
            // 保存文章到文件
            String filename_md = ArticleUtil.saveArticleToFile(article).getName();

            // 更新内存中的文章数据
            articleDataCore.put(filename_md.substring(0, filename_md.lastIndexOf(".")), article);

            return Result.ok("编辑文章成功");
        } catch (IOException e) {
            throw new BizException("编辑文章失败");
        }
    }

    /**
     * 删除文章
     * @param article 文章对象
     * @return Result对象
     */
    public static Result deleteArticle(Article article){

        // 删除内存中的文章数据
        articleDataCore.remove(article.getFileName());
        
        // 删除文件
        if (FileUtil.deleteFile(article.getFileName() + ".md", file_root_path)) {
            // 重新初始化分类和标签数据，确保索引正确
            reInitcategoriesData();
            return Result.ok("删除文章成功");
        } else {
            throw new BizException("删除文章失败");
        }
    }

    /**
     * 根据分类查询文章
     * @param category 分类名称
     * @return 该分类下的文章列表
     */
    public static List<Article> getArticlesByCategory(String category) {
        if (category == null || category.isEmpty()) {
            // 如果分类为空，返回所有文章
            List<Article> allArticles = new ArrayList<>();
            articleDataCore.forEach((k, v) -> allArticles.add((Article) v));
            return allArticles;
        }
        return categoryIndex.getOrDefault(category, new ArrayList<>());
    }

    /**
     * 根据标签查询文章
     * @param tag 标签名称
     * @return 包含该标签的文章列表
     */
    public static List<Article> getArticlesByTag(String tag) {
        if (tag == null || tag.isEmpty()) {
            // 如果标签为空，返回所有文章
            List<Article> allArticles = new ArrayList<>();
            articleDataCore.forEach((k, v) -> allArticles.add((Article) v));
            return allArticles;
        }
        return tagIndex.getOrDefault(tag, new ArrayList<>());
    }

    /**
     * 根据关键词搜索文章
     * @param keyword 搜索关键词
     * @return 匹配的文章列表
     */
    public static List<Article> searchArticles(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            // 如果关键词为空，返回所有文章
            List<Article> allArticles = new ArrayList<>();
            articleDataCore.forEach((k, v) -> allArticles.add((Article) v));
            return allArticles;
        }

        List<Article> matchedArticles = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (Object value : articleDataCore.values()) {
            Article article = (Article) value;
            // 搜索标题
            if (article.getTitle() != null && article.getTitle().toLowerCase().contains(lowerKeyword)) {
                matchedArticles.add(article);
                continue;
            }
            // 搜索分类
            if (article.getCategories() != null) {
                for (String category : article.getCategories()) {
                    if (category.toLowerCase().contains(lowerKeyword)) {
                        matchedArticles.add(article);
                        break;
                    }
                }
                if (matchedArticles.contains(article)) continue;
            }
            // 搜索标签
            if (article.getTags() != null) {
                for (String tag : article.getTags()) {
                    if (tag.toLowerCase().contains(lowerKeyword)) {
                        matchedArticles.add(article);
                        break;
                    }
                }
                if (matchedArticles.contains(article)) continue;
            }
            // 搜索日期（转换为字符串后搜索）
            if (article.getDate() != null) {
                String dateStr = article.getDate().toString().toLowerCase();
                if (dateStr.contains(lowerKeyword)) {
                    matchedArticles.add(article);
                }
            }
        }

        return matchedArticles;
    }
}
