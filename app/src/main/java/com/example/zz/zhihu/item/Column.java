package com.example.zz.zhihu.item;

public class Column {
    String username;
    String name;
    String id;
    String description;
    String thumbnail;
    public Column(String username,String name,String id,String description,String thumbnail){
        this.username=username;
        this.name=name;
        this.id=id;
        this.description=description;
        this.thumbnail=thumbnail;
    }
    public String getUsername(){
        return username;
    }
    public String getName(){
        return name;
    }
    public String getId(){
        return id;
    }
    public String getDescription(){
        return description;
    }
    public String getThumbnail(){
        return thumbnail;
    }
}
