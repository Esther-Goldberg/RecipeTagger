package com.example.recipetagger.models;

import android.net.Uri;

import com.example.recipetagger.utils.UriJsonSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class DocumentModel {
    private final long docID;
    private final String docName;
    private final Uri docUri;
    private ArrayList<TagModel> tags;

    public DocumentModel(long docID, String docName, Uri docUri) {
        this.docID = docID;
        this.docName = docName;
        this.docUri = docUri;
        this.tags = new ArrayList<>();
    }

    public ArrayList<TagModel> getTags() {
        return tags;
    }

    public void setTags(ArrayList<TagModel> tags) {
        this.tags = tags;
    }

    public void addTag(TagModel tag) {
        this.tags.add(tag);
    }

    public long getDocID() {
        return docID;
    }

    public String getDocName() {
        return docName;
    }

    public Uri getDocUri() {
        return docUri;
    }

    public String getJson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriJsonSerializer()).create();
        return gson.toJson(this);
    }

    public static DocumentModel getDocumentFromJson(String jsonDocumentModel) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriJsonSerializer()).create();
        return gson.fromJson(jsonDocumentModel, DocumentModel.class);
    }

}
