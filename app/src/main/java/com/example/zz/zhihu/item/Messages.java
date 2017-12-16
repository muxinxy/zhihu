package com.example.zz.zhihu.item;


public class Messages {
    String username;
    String title;
    String id;
    private String display_date;
    private String images;
    //private String LCN;
    //private String SCN;
    public Messages(String username, String title, String id, String display_date, String images){
        this.username=username;
        this.title=title;
        this.id=id;
        this.display_date=display_date;
        this.images=images;
        //this.LCN=LCN;
        //this.SCN=SCN;
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
    /*String getLCN(){
        return LCN;
    }
    String getSCN(){
        return SCN;
    }*/
}

