package com.android.dynamic_dictionary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class WordSharedActivity extends AppCompatActivity implements WebDictionary.WebDictionaryResponseListener {
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
        WebDictionary.sendDefinitionRequest(this, word, null);

        findViewById(R.id.buttonWordShared_Cancel).setOnClickListener(v -> finish());
        findViewById(R.id.buttonWordShared_Add).setOnClickListener(v -> {
            addWord(word);
            finish();
        });
    }

    public void addWord(String word) {
        // add word
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        if (!word.equals("")) {
            DatabaseHelper.Returns added = dbHelper.add(new WordEntry(word, "", responseData));
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
    public void handleResponseData(String word, String responseData, WebDictionary.Responses responseType, VariationsPagerAdapter.ViewPagerViewHolder holder) {
        Context context = this;
        this.responseData = responseData;
        ProgressBar progressBar = findViewById(R.id.progressBarWordShared_Dict);
        TextView textView = findViewById(R.id.textViewWordShared_DictError);
        Button button = findViewById(R.id.buttonWordShared_Retry);
        ViewPager2 pager = findViewById(R.id.pagerWordShared_Variations);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        button.setOnClickListener(v -> {
            pager.setVisibility(View.GONE);
            button.setVisibility(View.INVISIBLE);
            button.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);
            WebDictionary.sendDefinitionRequest(context, word, holder);
        });
        progressBar.setVisibility(View.GONE);
        button.setClickable(true);
        switch (responseType) {
            case SUCCESS:
                pager.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                button.setVisibility(View.GONE);

                WordEntry e = new WordEntry(word, "", responseData);
                VariationsPagerAdapterNoNotes pagerAdapter = new VariationsPagerAdapterNoNotes(this, e.getWordVariations());
                pager.setAdapter(pagerAdapter);
                break;
            case NO_DATA:
                pager.setVisibility(View.GONE);
                button.setVisibility(View.VISIBLE);
                textView.setText(R.string.error_undefined);
                break;
            case RESPONSE_ERROR:
                pager.setVisibility(View.GONE);
                button.setVisibility(View.VISIBLE);
                textView.setText(R.string.error_dict);
                break;
            case NO_RESPONSE:
                pager.setVisibility(View.GONE);
                button.setVisibility(View.VISIBLE);
                textView.setText(R.string.error_no_internet);
                break;
        }
    }
}
