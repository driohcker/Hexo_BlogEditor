package org.sxi.vo;

import lombok.Data;

import java.util.List;
import java.util.Date;

/**
 * 文章对象类，用于存储文章的元数据和内容
 */
@Data
public class Article {
    private String title;
    private Date date;
    private List<String> categories;
    private List<String> tags;
    private String content;
    private String fileName;
    
    /**
     * 无参构造方法
     */
    public Article() {
    }
    
    /**
     * 全参构造方法
     */
    public Article(String title, Date date, List<String> categories, List<String> tags) {
        this.title = title;
        this.date = date;
        this.categories = categories;
        this.tags = tags;
    }
    
    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", date=" + date +
                ", categories=" + categories +
                ", tags=" + tags +
                '}';
    }
}