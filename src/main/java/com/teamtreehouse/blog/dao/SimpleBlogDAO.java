package com.teamtreehouse.blog.dao;

import com.teamtreehouse.blog.model.BlogEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by romanmayer on 07/03/2017.
 */
public class SimpleBlogDAO implements BlogDao {

    private List<BlogEntry> entries;

    public SimpleBlogDAO() {
        this.entries = new ArrayList<>();
    }

    @Override
    public boolean addEntry(BlogEntry blogEntry) {
        return entries.add(blogEntry);
    }

    @Override
    public boolean deleteEntry(BlogEntry blogEntry) {
        return entries.remove(blogEntry);
    }

    @Override
    public List<BlogEntry> findAllEntries() {
        return new ArrayList<>(entries);
    }

    @Override
    public BlogEntry findEntryBySlug(String slug) {
        return (entries.stream()
                .filter(idea -> idea.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(NotFoundException::new));
    }

}
