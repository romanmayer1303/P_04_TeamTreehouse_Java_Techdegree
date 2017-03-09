package com.teamtreehouse.blog.model;

import java.util.Date;

public class Comment {

    private Date date;
    private String author;
    private String text;

    public Comment(String author, String text) {
        this.date = new Date();
        this.author = author;
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
}
