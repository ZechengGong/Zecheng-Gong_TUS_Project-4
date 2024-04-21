package com.example.petvillage;

import java.util.ArrayList;
import java.util.List;

public class Model {
    String tittle, desc, author, date, img, share_count,id,timestamp;
    List<Comment> comments;

    private String userId;

    private int likes;

    public Model(String tittle, String desc, String author, String date, String img, String share_count, String id, String timestamp, String userId) {
        this.tittle = tittle;
        this.desc = desc;
        this.author = author;
        this.date = date;
        this.img = img;
        this.share_count = share_count;
        this.id = id;
        this.userId = userId;
        this.timestamp = timestamp;
        this.comments = new ArrayList<>();
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Model() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return tittle;
    }

    public void setTitle(String tittle) {
        this.tittle = tittle;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getShare_count() {
        return share_count;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setShare_count(String share_count) {
        this.share_count = share_count;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

}