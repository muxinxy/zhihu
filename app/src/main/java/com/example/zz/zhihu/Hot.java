package com.example.zz.zhihu;

public class Hot {
    String username;
    String title;
    private String news_id;
    String thumbnail;
    String url;
    private String LCN;
    private String SCN;
    Hot(String username, String title, String news_id, String thumbnail, String url, String LCN, String SCN){
        this.username=username;
        this.title=title;
        this.news_id=news_id;
        this.thumbnail=thumbnail;
        this.url=url;
        this.LCN=LCN;
        this.SCN=SCN;
    }
    public String getUsername(){
        return username;
    }
    public String getTitle(){
        return title;
    }
    String getNews_id(){
        return news_id;
    }
    public String getThumbnail(){
        return thumbnail;
    }
    public String getUrl(){
        return url;
    }
    String getLCN(){
        return LCN;
    }
    String getSCN(){
        return SCN;
    }
}

