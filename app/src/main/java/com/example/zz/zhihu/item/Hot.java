package com.example.zz.zhihu.item;

public class Hot {
    String username;
    String title;
    private String news_id;
    String thumbnail;
    String url;
    public Hot(String username, String title, String news_id, String thumbnail, String url){
        this.username=username;
        this.title=title;
        this.news_id=news_id;
        this.thumbnail=thumbnail;
        this.url=url;
    }
    public String getUsername(){
        return username;
    }
    public String getTitle(){
        return title;
    }
    public String getNews_id(){
        return news_id;
    }
    public String getThumbnail(){
        return thumbnail;
    }
    public String getUrl(){
        return url;
    }
}

