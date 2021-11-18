package com.android.dynamic_dictionary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WordSharedActivity extends Activity implements WebDictionary.WebDictionaryResponseListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String word = intent.getClipData().getItemAt(0).getText().toString().split("\n")[0].split("\"")[1];
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.add(new WordEntry(word, "", ""));
        Toast.makeText(this, "Saved " + " " + word, Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    public void handleResponseData(String word, String response) {
    }
}
