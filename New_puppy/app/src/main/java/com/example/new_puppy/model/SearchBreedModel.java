package com.example.new_puppy.model;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SearchBreedModel implements Searchable {
    private int id;
    private String mTitle;

    public SearchBreedModel(int id,String title) {
        this.id = id;
        this.mTitle = title;
    }

    public int getId() {
        return id;
    }

    public SearchBreedModel setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public SearchBreedModel setTitle(String title) {
        mTitle = title;
        return this;
    }
}

