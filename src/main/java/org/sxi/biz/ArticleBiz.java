package org.sxi.biz;

import org.sxi.dao.ArticleDataCore;
import org.sxi.dao.PropertiesDataCore;
import org.sxi.vo.Article;

import java.io.File;
import java.io.IOException;

public class ArticleBiz {

    public void addArticle(Article article){
        String fileName = article.getFileName();
        File file = new File(PropertiesDataCore.getProperty("post.root.path") + fileName + ".md");
        if (file.exists()){
            throw new BizException("已存在相同标题的文章");
        }

        ArticleDataCore.addArticle(article);
    }

    public void editArticle(Article article){
        if (article == null || article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            throw new BizException("文章标题不能为空");
        }
        
        // 检查文章是否存在
        String filePath = PropertiesDataCore.getStringProperty("post.root.path") + article.getFileName() + ".md";
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("要编辑的文章不存在, 立即新建" + filePath);
        }
        
        // 调用数据层方法编辑文章
        ArticleDataCore.editArticle(article);
    }

    public void deleteArticle(Article article){
        if (article == null || article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            throw new BizException("文章标题不能为空");
        }
        
        // 检查文章是否存在
        String filePath = PropertiesDataCore.getStringProperty("post.root.path") + article.getFileName() + ".md";
        System.out.println("要删除： "+ filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new BizException("要删除的文章不存在");
        }
        
        // 调用数据层方法删除文章
        ArticleDataCore.deleteArticle(article);
    }
}
