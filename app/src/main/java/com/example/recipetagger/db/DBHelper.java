package com.example.recipetagger.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.recipetagger.db.DocumentsContract;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RecipeTagger.db";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DocumentsContract.SQL_CREATE_DOCUMENT_TABLE);
        db.execSQL(DocumentsContract.SQL_CREATE_TAG_TABLE);
        db.execSQL(DocumentsContract.SQL_CREATE_DOC_TAG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // no upgrades yet
    }
}
