package com.example.petvillage.Models;

import java.util.ArrayList;
import java.util.List;

public class Model_Post {
    private String title, content, nickname, date, img, id;
    private long timestamp;
    private List<Model_Comment> modelComments;
    private String userId;
    private int likes;

    public Model_Post(String title, String content, String nickname, String date, String img, String id, long timestamp, String userId) {
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.date = date;
        this.img = img;
        this.id = id;
        this.userId = userId;
        this.timestamp = timestamp;
        this.modelComments = new ArrayList<>();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Model_Post() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNickname() {
        return nickname;
    }

    public void setAuthor(String author) {
        this.nickname = nickname;
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

    public List<Model_Comment> getComments() {
        return modelComments;
    }

    public void setComments(List<Model_Comment> modelComments) {
        this.modelComments = modelComments;
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