package com.example.zz.zhihu;

public class Hot {
    String title;
    String news_id;
    String thumbnail;
    String url;
    public Hot(String title,String news_id,String thumbnail,String url){
        this.title=title;
        this.news_id=news_id;
        this.thumbnail=thumbnail;
        this.url=url;
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

