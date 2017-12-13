package com.example.zz.zhihu;


public class Message {
    String username;
    String title;
    String id;
    String display_date;
    String images;
    public Message(String username,String title,String id,String display_date,String images){
        this.username=username;
        this.title=title;
        this.id=id;
        this.display_date=display_date;
        this.images=images;
    }
    public String getUsername(){
        return username;
    }
    public String getTitle(){
        return title;
    }
    public String getId(){
        return id;
    }
    public String getDisplay_date(){
        return display_date;
    }
    public String getImages(){
        return images;
    }
}

