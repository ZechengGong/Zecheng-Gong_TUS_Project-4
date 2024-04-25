package com.example.petvillage.Models;
import com.google.firebase.database.ServerValue;

public class Model_Comment {
    private String content,uid, uImg,uname;
    private Object timestamp;

    public Model_Comment() {
    }

    public Model_Comment(String content, String uid, String uImg, String uname) {
        this.content = content;
        this.uid = uid;
        this.uImg = uImg;
        this.uname = uname;
        this.timestamp = ServerValue.TIMESTAMP;

    }

    public Model_Comment(String content, String uid, String uImg, String uname, Object timestamp) {
        this.content = content;
        this.uid = uid;
        this.uImg = uImg;
        this.uname = uname;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuImg() {
        return uImg;
    }

    public void setuImg(String uImg) {
        this.uImg = uImg;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}


