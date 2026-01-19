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
    public static Map<String, Object> articleDataCore = new HashMap<>();

    @Getter
    public static List<String> categoriesDataCore = new ArrayList<>();

    private static String file_root_path = PropertiesDataCore.getStringProperty("post.root.path");

    static {
        initArtcleData();
        initcategoriesData();
    }

    public static Result initArtcleData() {
        String[] files = FileUtil.listFiles(file_root_path);
        
        // 如果没有文件，返回成功结果，不抛出异常
        if (files.length == 0) {
            System.out.println("暂无文章");
            return Result.ok("暂无文章", null);
        }

        Arrays.stream(files).forEach(file -> {
            try {
                articleDataCore.put(file.substring(0, file.lastIndexOf(".")), ArticleUtil.readArticleFromFile(new File(file_root_path + file)));
            } catch (IOException | ParseException e) {
                System.err.println("加载文章失败: " + file + ", 错误信息: " + e.getMessage());
                // 不抛出异常，继续处理其他文件
            }
        });
        
        System.out.println("成功加载 " + articleDataCore.size() + " 篇文章");
        articleDataCore.forEach((k, v) -> {
            System.out.println(k + "   " + v);
        });
        
        return Result.ok("加载文章数据成功", null);
    }

    public static Result initcategoriesData() {
        String[] files = FileUtil.listFiles(file_root_path);
        
        // 如果没有文件，返回成功结果，不抛出异常
        if (files.length == 0) {
            System.out.println("暂无文章，无法加载分类数据");
            return Result.ok("暂无文章，无法加载分类数据");
        }

        Arrays.stream(files).forEach(file -> {
            try {
                Article article = ArticleUtil.readArticleFromFile(new File(file_root_path + file));
                if (article.getCategories() != null && !article.getCategories().isEmpty()) {
                    String category = article.getCategories().get(0);
                    if (!categoriesDataCore.contains(category)) {
                        categoriesDataCore.add(category);
                    }
                }
            } catch (IOException | ParseException | IndexOutOfBoundsException e) {
                System.err.println("加载文章分类失败: " + file + ", 错误信息: " + e.getMessage());
                // 不抛出异常，继续处理其他文件
            }
        });

        System.out.println("成功加载 " + categoriesDataCore.size() + " 个分类");
        return Result.ok("加载分类数据成功");
    }

    public static Result reInitArtcleData() {
        // 重新获取最新的配置路径
        file_root_path = PropertiesDataCore.getStringProperty("post.root.path");
        // 清空旧数据
        articleDataCore.clear();
        // 重新初始化数据
        return initArtcleData();
    }

    public static Result reInitcategoriesData() {
        // 重新获取最新的配置路径
        file_root_path = PropertiesDataCore.getStringProperty("post.root.path");
        // 清空旧数据
        categoriesDataCore.clear();
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
        // 构造文件路径
        String filePath = file_root_path + article.getFileName() + ".md";
        
        // 删除内存中的文章数据
        articleDataCore.remove(filePath);
        
        // 删除文件
        if (FileUtil.deleteFile(article.getFileName() + ".md", file_root_path)) {
            return Result.ok("删除文章成功");
        } else {
            throw new BizException("删除文章失败");
        }
    }
}
