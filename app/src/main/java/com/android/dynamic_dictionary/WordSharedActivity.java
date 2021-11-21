package com.android.dynamic_dictionary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class WordSharedActivity extends AppCompatActivity implements WebDictionary.WebDictionaryResponseListener {
    private ViewPager2 pagerVariations;
    private VariationsPagerAdapterNoNotes pagerAdapter;
    private String responseData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_shared);

        // get text data from intent, get word from text data
        Intent intent = getIntent();
        String wordTmp = intent.getClipData().getItemAt(0).getText().toString().split("\n")[0].replaceAll("\"", "");
        setTitle("Add \"" + wordTmp + "\"?");
        if (wordTmp.length() > 1)
            wordTmp = wordTmp.substring(0, 1).toUpperCase() + wordTmp.substring(1);
        final String word = wordTmp;

        // request definition from the web dictionary
        WebDictionary.sendDefinitionRequest(this, word);

        findViewById(R.id.buttonWordShared_Cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.buttonWordShared_Add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWord(word);
                finish();
            }
        });
    }


    public void addWord(String word) {
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
    }

    @Override
    public void handleResponseData(String word, String responseData, WebDictionary.Responses responseType) {
        this.responseData = responseData;
        findViewById(R.id.progressBarWordShared).setVisibility(View.GONE);
        switch (responseType) {
            case SUCCESS:
                findViewById(R.id.pagerWordShared_Variations).setVisibility(View.VISIBLE);
                findViewById(R.id.textViewWordShared_DictError).setVisibility(View.GONE);
                findViewById(R.id.buttonWordShared_Retry).setVisibility(View.GONE);

                pagerVariations = findViewById(R.id.pagerWordShared_Variations);
                WordEntry e = new WordEntry(word, "", responseData);
                pagerAdapter = new VariationsPagerAdapterNoNotes(this, e.getWordVariations(), e.getWord(), e.getDescription());
                pagerVariations.setAdapter(pagerAdapter);
                break;
            case NO_DATA:
                findViewById(R.id.pagerWordShared_Variations).setVisibility(View.GONE);
                findViewById(R.id.textViewWordShared_DictError).setVisibility(View.VISIBLE);
                findViewById(R.id.buttonWordShared_Retry).setVisibility(View.VISIBLE);

                ((TextView)findViewById(R.id.textViewWordShared_DictError)).setText("Undefined word");
                break;
            case RESPONSE_ERROR:
                findViewById(R.id.pagerWordShared_Variations).setVisibility(View.GONE);
                findViewById(R.id.textViewWordShared_DictError).setVisibility(View.VISIBLE);
                findViewById(R.id.buttonWordShared_Retry).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.textViewWordShared_DictError)).setText("Dictionary error");
                break;
            case NO_RESPONSE:
                findViewById(R.id.pagerWordShared_Variations).setVisibility(View.GONE);;
                findViewById(R.id.textViewWordShared_DictError).setVisibility(View.VISIBLE);
                findViewById(R.id.buttonWordShared_Retry).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.textViewWordShared_DictError)).setText("No Internet");
                break;
        }
    }
}
