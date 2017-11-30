package com.example.zz.zhihu;

public class Column {
    String name;
    String id;
    String description;
    String thumbnail;
    public Column(String name,String id,String description,String thumbnail){
        this.name=name;
        this.id=id;
        this.description=description;
        this.thumbnail=thumbnail;
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
