package com.example.recipetagger.activities;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.example.recipetagger.R;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.recipetagger.classes.OnItemClickListener;
import com.example.recipetagger.classes.RecipeListItemAdapter;
import com.example.recipetagger.databinding.ActivityMainBinding;
import com.example.recipetagger.db.DocumentsContract.DocumentEntry;
import com.example.recipetagger.models.DocumentModel;
import com.example.recipetagger.db.DBHelper;
import com.example.recipetagger.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private String mQuery;
    private DBHelper mDBHelper;
    private ContentResolver mResolver;
    private RecipeListItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(view -> openDocumentPicker());

        mDBHelper = new DBHelper(getApplicationContext());
        mResolver = getContentResolver();

        handleIntent(getIntent());

        //TODO: remove or not showDocumentsList();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(@NonNull Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
        } else {
            mQuery = null;
        }
        showDocumentsList();
    }

    private void openDocumentPicker() {
        // Find new files to add to the display in the app
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        documentPickerLauncher.launch(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Utils.showInfoDialog(this, "About",
                    "Author: Esther Stein\nVersion: 1.0\n\n" +
                            "Add PDF files of recipes you've saved, and tag them with categories, " +
                            "ingredients and more! Search using tags you've created " +
                            "so you can easily find exactly what you're looking for.");
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            settingsLauncher.launch(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> saveSettings());

    ActivityResultLauncher<Intent> documentPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                saveDocument(result.getData().getData());
                }
            });

    private void saveSettings() {
    }

    private void showDocumentsList() {
        mAdapter = new RecipeListItemAdapter(mQuery, mDBHelper);
        mAdapter.setOnItemClickListener(getNewOnItemClickListener());
        RecyclerView rvDocuments  = binding.contentMain.recipeList;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvDocuments.setLayoutManager(layoutManager);
        rvDocuments.setAdapter(mAdapter);
    }
    private void saveDocument(Uri docURI) {
        mResolver.takePersistableUriPermission(docURI, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        String fileName = getDisplayName(docURI);
        String docName = fileName.contains(".")?fileName.substring(0, fileName.lastIndexOf(".")):fileName;

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DocumentEntry.COLUMN_NAME_TITLE, docName);
        values.put(DocumentEntry.COLUMN_NAME_URI, docURI.toString());

        try {
            long docID = db.insert(DocumentEntry.TABLE_NAME, null, values);

            DocumentModel documentModel = new DocumentModel(docID, docName, docURI);
            mAdapter.notifyItemInserted(mAdapter.addDocToList(documentModel));
        } catch (SQLiteConstraintException e) {
            Snackbar.make(this, binding.getRoot(), "This document is has already been added!", Snackbar.LENGTH_SHORT).show();

        }
    }

    private String getDisplayName(Uri docURI) {
        try (Cursor c = getContentResolver().query(docURI, null, null, null, null)) {
            c.moveToFirst();
            int nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            return c.getString(nameIndex);
        }
    }

    public void openDocument(DocumentModel document) {
        Intent intent = new Intent(getApplicationContext(), DocumentViewerActivity.class);
        intent.putExtra("DOCUMENT", document.getJson());
        startActivity(intent);
    }

    private OnItemClickListener getNewOnItemClickListener() {
        return (position, view) -> {
            openDocument (mAdapter.getDocAtPosition(position));
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}