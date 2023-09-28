package com.example.recipetagger.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.recipetagger.db.DBHelper;
import com.example.recipetagger.db.DocumentsContract.TagEntry;
import com.example.recipetagger.db.DocumentsContract.DocumentTagEntry;
import com.example.recipetagger.models.DocumentModel;
import com.example.recipetagger.models.TagModel;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.core.content.res.ResourcesCompat;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.recipetagger.databinding.ActivityDocumentViewerBinding;

import com.example.recipetagger.R;

import java.util.ArrayList;
import java.util.Objects;

public class DocumentViewerActivity extends AppCompatActivity {

    private ActivityDocumentViewerBinding binding;

    DocumentModel mDocument;
    Flow mTagsBar;
    final String CURRENT_PAGE = "current_page";
    DBHelper mDBHelper;
    int numTagsOld = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDocumentViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        binding.fab.setOnClickListener(view -> openGetTagsActivity());

        mTagsBar = binding.contentDocumentViewer.tagsBar;

        mDBHelper = new DBHelper(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mDocument = DocumentModel.getDocumentFromJson(extras.getString("DOCUMENT"));
            binding.contentDocumentViewer.pdfView.fromUri(mDocument.getDocUri()).load();
            displayDocumentTags();
            binding.toolbar.setTitle(mDocument.getDocName());
        } else if (savedInstanceState != null) {
            mDocument = DocumentModel.getDocumentFromJson(savedInstanceState.getString("DOCUMENT"));
            int currentPage = savedInstanceState.getInt(CURRENT_PAGE);
            binding.contentDocumentViewer.pdfView
                    .fromUri(mDocument.getDocUri())
                    .defaultPage(currentPage)
                    .load();
        }
    }

    private void displayDocumentTags() {

        resetTagsLayout();

        for (TagModel tag : mDocument.getTags()) {

            TextView tvTag = new TextView(this);
            tvTag.setBackground(ResourcesCompat.getDrawable(this.getResources(), R.drawable.tag_image, this.getTheme()));
            tvTag.setText(tag.getTagName());
            tvTag.setTextColor(getColor(R.color.tag_text_color));
            //TODO: make this work eventually tvTag.setOnClickListener(v -> triggerSearch(tag.getTagName(), null));
            tvTag.setId(View.generateViewId());
            binding.contentDocumentViewer.contentDocumentViewer.addView(tvTag);
            binding.contentDocumentViewer.tagsBar.addView(tvTag);
        }

        numTagsOld = mDocument.getTags().size();
    }

    private void resetTagsLayout() {
        if (numTagsOld > 0) {
            binding.contentDocumentViewer.contentDocumentViewer.removeViews(2, numTagsOld);
        }
    }

    ActivityResultLauncher<Intent> tagsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    saveTags(result.getData().getCharSequenceArrayListExtra("TAGS"));
                }
            });

    private void saveTags(ArrayList<CharSequence> tags) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues values;
        for (CharSequence tag:tags) {
            String tagString = (String) tag;
            long tagID;
            Cursor cursor = db.query(
                    TagEntry.TABLE_NAME,
                    new String[]{TagEntry._ID},
                    TagEntry.COLUMN_NAME_TITLE + "= ?",
                    new String[]{tagString}, null, null, null
            );
            if (! cursor.moveToFirst()) {
                values = new ContentValues();
                values.put(TagEntry.COLUMN_NAME_TITLE, tagString);
                tagID = db.insert(TagEntry.TABLE_NAME, null, values);
            } else {
                tagID = cursor.getLong(0);
            }
            cursor.close();

            values = new ContentValues();
            values.put(DocumentTagEntry.COLUMN_NAME_DOCUMENT, mDocument.getDocID());
            values.put(DocumentTagEntry.COLUMN_NAME_TAG, tagID);
            db.insert(DocumentTagEntry.TABLE_NAME, null, values);

            mDocument.addTag(new TagModel(tagID, tagString));
        }

        displayDocumentTags();
    }

    private void openGetTagsActivity() {
        Intent intent = new Intent(getApplicationContext(), AddTagsActivity.class);
        tagsLauncher.launch(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_PAGE, binding.contentDocumentViewer.pdfView.getCurrentPage() );
        outState.putString("DOCUMENT", mDocument.getJson());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}