package com.android.dynamic_dictionary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WordSharedActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        // remove text after newline if it exists and remove quotation marks if they exist
        String word = intent.getClipData().getItemAt(0).getText().toString().split("\n")[0].replaceAll("\"", "");

        // add word
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        if (!word.equals("")) {
            DatabaseHelper.Returns added = dbHelper.add(new WordEntry(word, "", ""));
            if (added == DatabaseHelper.Returns.ADDED)
                Toast.makeText(this, "Saved " + word, Toast.LENGTH_SHORT).show();
            else if (added == DatabaseHelper.Returns.NOT_ADDED_ERROR)
                Toast.makeText(this, "Couldn't save " + word, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Word " + word + " already exists", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Word can't be blank", Toast.LENGTH_SHORT).show();
        }
        this.finish();
    }
}
