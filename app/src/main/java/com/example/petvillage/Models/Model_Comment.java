package com.example.petvillage.Models;
import com.google.firebase.database.ServerValue;

// Comment Model
public class Model_Comment {
    private String content,uid, uImg,uname;
    private Object timestamp;
    private String commentId;
    private String postId;
    public Model_Comment() {
    }

    public Model_Comment(String content, String uid, String uImg, String uname, String commentId, String postId) {
        this.content = content;
        this.uid = uid;
        this.uImg = uImg;
        this.uname = uname;
        this.timestamp = ServerValue.TIMESTAMP;
        this.commentId = commentId;
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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


