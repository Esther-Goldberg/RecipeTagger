package com.example.recipetagger.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import android.widget.TextView;

import com.example.recipetagger.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

public class AddTagsActivity extends AppCompatActivity {

    TextInputEditText addTags;
    MaterialButton saveBtn;
    TextView enteredTags;
    ArrayList<String> tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tags);


        tags = new ArrayList<>();
        enteredTags = findViewById(R.id.entered_tags);
        saveBtn = findViewById(R.id.btn_save_tags);
        saveBtn.setOnClickListener(v -> saveTagsAndExit());
        addTags = findViewById(R.id.enter_tags);
        addTags.setOnKeyListener((v, keyCode, event) -> addTag((TextInputEditText) v, keyCode, event));


    }

    private void saveTagsAndExit() {
        Intent intent = new Intent();
        intent.putExtra("TAGS", tags);
        setResult(RESULT_OK, intent);
        finish();
    }


    private boolean addTag(TextInputEditText v, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                String newTag = Objects.requireNonNull(v.getText()).toString();
                tags.add(newTag);
                v.setText("");
                enteredTags.setText(new StringBuilder().append(newTag).append("\n").append(enteredTags.getText()).toString());
                return true;
            }
            return false;
        }



}