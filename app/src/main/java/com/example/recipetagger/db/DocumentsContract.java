package com.example.recipetagger.db;

import android.provider.BaseColumns;

public class DocumentsContract {

    private DocumentsContract(){};

    public static class DocumentEntry implements BaseColumns {
        public static final String TABLE_NAME = "document";
        public static final String COLUMN_NAME_URI = "uri";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_THUMBNAIL = "thumbnail";

    }

    public static class TagEntry implements BaseColumns {
        public static final String TABLE_NAME = "tag";
        public static final String COLUMN_NAME_TITLE = "title";

    }

    public static class DocumentTagEntry implements BaseColumns {
        public static final String TABLE_NAME = "document_tag";
        public static final String COLUMN_NAME_TAG = "tag_id";
        public static final String COLUMN_NAME_DOCUMENT = "document_id";

    }

    public static final String SQL_CREATE_DOCUMENT_TABLE =
            "CREATE TABLE " + DocumentEntry.TABLE_NAME + " (" +
                    DocumentEntry._ID + " INTEGER PRIMARY KEY, " +
                    DocumentEntry.COLUMN_NAME_URI + " TEXT UNIQUE," +
                    DocumentEntry.COLUMN_NAME_TITLE + " TEXT," +
                    DocumentEntry.COLUMN_NAME_THUMBNAIL + " BLOB); ";

    public static final String SQL_CREATE_TAG_TABLE =
            "CREATE TABLE " + TagEntry.TABLE_NAME + " (" +
                    TagEntry._ID + " INTEGER PRIMARY KEY, " +
                    TagEntry.COLUMN_NAME_TITLE + " TEXT UNIQUE); ";

    public static final String SQL_CREATE_DOC_TAG_TABLE =
            "CREATE TABLE " + DocumentTagEntry.TABLE_NAME + " (" +
                    DocumentTagEntry._ID + " INTEGER PRIMARY KEY, " +
                    DocumentTagEntry.COLUMN_NAME_TAG + " INTEGER, " +
                    DocumentTagEntry.COLUMN_NAME_DOCUMENT + " INTEGER, " +
                    " FOREIGN KEY (" + DocumentTagEntry.COLUMN_NAME_DOCUMENT +
                        ") REFERENCES " + DocumentEntry.TABLE_NAME + "(" + DocumentEntry._ID + ")," +
                    " FOREIGN KEY (" + DocumentTagEntry.COLUMN_NAME_TAG +
                        ") REFERENCES " + TagEntry.TABLE_NAME + "(" + TagEntry._ID + ") );";

}
