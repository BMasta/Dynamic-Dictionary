package com.android.dynamic_dictionary;
import android.icu.util.Measure;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WordEntry {
    //--------------------------fields--------------------------//
    private String word;
    private String description;
    private String meaningsJson;
    private List<WordEntryMeaning> meanings;

    //-----------------------constructor------------------------//
    public WordEntry(String word, String description, String meaningsJson, boolean parseMeaningsNow) {
        super();
        this.word = word;
        this.description = description;
        this.meaningsJson = meaningsJson;
        if (parseMeaningsNow) {
            getMeanings();
        }
    }
    //-------------------------toString-------------------------//


    @Override
    public String toString() {
        return "WordEntry{" +
                "word='" + word + '\'' +
                ", description='" + description + '\'' +
                ", meaningsJson='" + meaningsJson + '\'' +
                ", meanings=" + meanings +
                '}';
    }

    //-------------------getters and setters--------------------//
    public String getWord() {
        return word;
    }

    public String getDescription() {
        return description;
    }

    public List<WordEntryMeaning> getMeanings() {
        if (meanings == null) {
            try {
                meanings = new ArrayList<>();
                JSONArray meaningsParsed = (new JSONArray(meaningsJson)).getJSONObject(0).getJSONArray("meanings");
                for (int i = 0; i < meaningsParsed.length(); ++i) {
                    meanings.add(new WordEntryMeaning(meaningsParsed.getJSONObject(i), false));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return meanings;
    }

    public String getMeaningsJson() {
        return meaningsJson;
    }

    //-----------------------------------------------------------//
}
