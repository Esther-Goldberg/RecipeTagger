package com.example.recipetagger.models;

import java.io.Serializable;

public class TagModel implements Serializable {

    private long tagID;
    private String tagName;

    public TagModel(long tagID, String tagName) {
        this.tagID = tagID;
        this.tagName = tagName;
    }

    public long getTagID() {
        return tagID;
    }

    public String getTagName() {
        return tagName;
    }
}
