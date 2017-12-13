package com.example.zz.zhihu;

public class LongCommits {
    private String author;
    String id;
    String content;
    private String avatar;
    private String likes;
    private String time;
    private String reply_content;
    private String reply_status;
    private String reply_id;
    private String reply_author;
    private String reply_err_msg;
    private String JsonLength;
    LongCommits(String author, String id, String content, String avatar, String likes, String time, String reply_content, String reply_status, String reply_id, String reply_author, String reply_err_msg, String JsonLength){
        this.author=author;
        this.id=id;
        this.content=content;
        this.avatar =avatar ;
        this.likes=likes;
        this.time=time;
        this.reply_content=reply_content;
        this.reply_status=reply_status;
        this.reply_id=reply_id;
        this.reply_author=reply_author;
        this.reply_err_msg=reply_err_msg;
        this.JsonLength=JsonLength;
    }
    String getAuthor(){
        return author;
    }
    public String getId(){
        return id;
    }
    public String getContent(){
        return content;
    }
    String getAvatar(){
        return avatar ;
    }
    String getLikes(){
        return likes;
    }
    public String getTime(){
        return time;
    }
    String getReply_content(){
        return reply_content;
    }
    public String getReply_status(){
        return reply_status;
    }
    public String getReply_id(){
        return reply_id;
    }
    String getReply_author(){
        return reply_author;
    }
    public String getReply_err_msg(){
        return reply_err_msg;
    }
    public String getJsonLength(){
        return JsonLength;
    }
}
