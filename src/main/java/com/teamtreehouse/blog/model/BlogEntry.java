package com.teamtreehouse.blog.model;

import com.github.slugify.Slugify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlogEntry {

    private String author;
    private Date date;
    private String text;
    private String title;
    private List<Comment> commentList;
    private String slug;
    private List<String> tags;

    public BlogEntry(String author, String title, String text) {
        this.author = author;
        this.date = new Date();
        this.text = text;
        this.title = title;
        this.commentList = new ArrayList<>();
        Slugify slugify = new Slugify();
        this.slug = slugify.slugify(title);
        this.tags = new ArrayList<>();
    }

    public BlogEntry(String author, String title, String text, ArrayList<String> tags) {
        this.author = author;
        this.date = new Date();
        this.text = text;
        this.title = title;
        this.commentList = new ArrayList<>();
        Slugify slugify = new Slugify();
        this.slug = slugify.slugify(title);
        this.tags = tags;
    }



    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public String getSlug() {
        return slug;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTitle(String title) {
        this.title = title;
        Slugify slugify = new Slugify();
        this.slug = slugify.slugify(title);
    }


    public boolean addComment(Comment comment) {
        return commentList.add(comment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlogEntry blogEntry = (BlogEntry) o;

        if (author != null ? !author.equals(blogEntry.author) : blogEntry.author != null) return false;
        if (date != null ? !date.equals(blogEntry.date) : blogEntry.date != null) return false;
        if (text != null ? !text.equals(blogEntry.text) : blogEntry.text != null) return false;
        return commentList != null ? !commentList.equals(blogEntry.commentList) : blogEntry.commentList != null;
    }

    @Override
    public int hashCode() {
        int result = author != null ? author.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (commentList != null ? commentList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BlogEntry{" +
                "author='" + author + '\'' +
                ", date=" + date +
                ", text='" + text + '\'' +
                ", commentList=" + commentList +
                '}';
    }


}
