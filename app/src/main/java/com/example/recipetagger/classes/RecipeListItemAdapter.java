package com.example.recipetagger.classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipetagger.R;
import com.example.recipetagger.db.DBHelper;
import com.example.recipetagger.db.DocumentsContract.TagEntry;
import com.example.recipetagger.db.DocumentsContract.DocumentEntry;
import com.example.recipetagger.db.DocumentsContract.DocumentTagEntry;
import com.example.recipetagger.models.DocumentModel;
import com.example.recipetagger.models.TagModel;

import java.util.ArrayList;

public class RecipeListItemAdapter extends RecyclerView.Adapter<RecipeListItem> {

    static OnItemClickListener onItemClickListener;
    private final ArrayList<DocumentModel> documentsList = new ArrayList<>();
    private final DBHelper dbHelper;

    public RecipeListItemAdapter(String query, DBHelper helper) {
        dbHelper = helper;
        fillDocumentsList(query);
    }

    private void fillDocumentsList(String query) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        // query contains a tag, so we first get the tag ID for it
        TagModel tagModel;
        if (query == null) {
            tagModel = new TagModel(0, null);
        } else {
            cursor = db.query(TagEntry.TABLE_NAME,
                    new String[]{TagEntry._ID, TagEntry.COLUMN_NAME_TITLE},
                    TagEntry.COLUMN_NAME_TITLE + " = ?",
                    new String[]{query},
                    null,
                    null,
                    null);

            if (cursor.moveToFirst()) {
                tagModel = new TagModel(cursor.getLong(0), cursor.getString(1));
            } else {
                tagModel = new TagModel(0, null);
                //TODO: show error
            }
            cursor.close();

        }

        // now we can query for the documents with this tag
        String documentsQuery;
        String[] selectionArgs;
        String tagsQuery = "SELECT * FROM " + TagEntry.TABLE_NAME
                + " INNER JOIN " + DocumentTagEntry.TABLE_NAME + " ON "
                + TagEntry.TABLE_NAME + "." + TagEntry._ID + " = "
                + DocumentTagEntry.TABLE_NAME + "." + DocumentTagEntry.COLUMN_NAME_TAG
                + " INNER JOIN " + DocumentEntry.TABLE_NAME + " ON "
                + DocumentEntry.TABLE_NAME + "." + DocumentEntry._ID + " = "
                + DocumentTagEntry.TABLE_NAME + "." + DocumentTagEntry.COLUMN_NAME_DOCUMENT
                + " WHERE " + DocumentTagEntry.COLUMN_NAME_DOCUMENT + " = ?";

        if (tagModel.getTagID() != 0) { // don't filter by tag, just display all documents
            documentsQuery = "SELECT * FROM " + DocumentEntry.TABLE_NAME
                    + " INNER JOIN " + DocumentTagEntry.TABLE_NAME + " ON "
                    + DocumentEntry.TABLE_NAME + "." + DocumentEntry._ID + " = "
                    + DocumentTagEntry.TABLE_NAME + "." + DocumentTagEntry.COLUMN_NAME_DOCUMENT
                    + " WHERE " + DocumentTagEntry.TABLE_NAME + "." + DocumentTagEntry.COLUMN_NAME_TAG + " = ?";

            selectionArgs = new String[] {String.valueOf(tagModel.getTagID())};
        } else {
            documentsQuery = "SELECT * FROM " + DocumentEntry.TABLE_NAME;
            selectionArgs = null;
        }

        // finally, we can query for all the tags on each document
        try (Cursor newCursor = db.rawQuery(documentsQuery, selectionArgs)) {
            DocumentModel dm;
            TagModel tm;
            while (newCursor.moveToNext()) {
                dm = new DocumentModel(newCursor.getLong(0),
                        newCursor.getString(2),
                        Uri.parse(newCursor.getString(1)));
                documentsList.add(dm);

                //query for tags and add them to dm
                cursor = db.rawQuery(tagsQuery, new String[]{String.valueOf(dm.getDocID())});
                while (cursor.moveToNext()) {
                    tm = new TagModel(cursor.getLong(0),
                            cursor.getString(1));
                    dm.addTag(tm);
                }
                cursor.close();
            }
        }

        // after all this, documentsList contains all the documents we want, with all their tags

    }

    @NonNull
    @Override
    public RecipeListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from (parent.getContext ()).inflate (
                R.layout.recipe_list_item, parent, false);
        return new RecipeListItem(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeListItem holder, int position) {
        DocumentModel dm = documentsList.get(position);
        holder.docName.setText(dm.getDocName());
        for (TagModel tag : dm.getTags()) {
            Activity activity = (Activity) holder.docListItem.getContext();
            TextView tvTag = new TextView(activity);
            tvTag.setBackground(ResourcesCompat.getDrawable(holder.docListItem.getResources(), R.drawable.tag_image, activity.getTheme()));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 16, 0);
            tvTag.setLayoutParams(lp);
            tvTag.setText(tag.getTagName());
            tvTag.setTextColor(activity.getColor(R.color.tag_text_color));
            tvTag.setOnClickListener(v -> activity.triggerSearch(tag.getTagName(), null));
            tvTag.setId(View.generateViewId());
            holder.docListItemLayout.addView(tvTag);
            holder.tagsFlow.addView(tvTag);
        }
    }

    @Override
    public int getItemCount() {
        return documentsList.size();
    }

    public void setOnItemClickListener (OnItemClickListener l) {
        onItemClickListener = l;
    }

    public DocumentModel getDocAtPosition(int position) {
        return documentsList.get(position);
    }

    /**
     * Takes a DocumentModel object, adds it to the list of documents, and returns its position
     * @param documentModel a document to add to the list of documents
     * @return the position of the document that was added
     */
    public int addDocToList(DocumentModel documentModel) {
        documentsList.add(documentModel);
        return (documentsList.size() - 1);
    }

}
