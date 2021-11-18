package com.android.dynamic_dictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WordEntryWord {
    //--------------------------fields--------------------------//
    private String wordVariation;
    private JSONObject wordJson;
    private List<WordEntryMeaning> meanings;
    //-----------------------constructor------------------------//
    public WordEntryWord(String wordVariety, JSONObject wordJson) {
        this.wordVariation = wordVariety;
        this.wordJson = wordJson;
    }
    //-------------------getters and setters--------------------//
    public String getWordVariety() {
        return wordVariation;
    }

    public JSONObject getWordJson() {
        return wordJson;
    }

    public List<WordEntryMeaning> getMeanings() {
        if (meanings == null) {
            try {
                meanings = new ArrayList<>();
                JSONArray meaningsParsed = wordJson.getJSONArray("meanings");
                for (int i = 0; i < meaningsParsed.length(); ++i) {
                    meanings.add(new WordEntryMeaning(meaningsParsed.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) { }

        }
        return meanings;
    }
    //-------------------------toString-------------------------//
    @Override
    public String toString() {
        getMeanings();
        return "WordEntryWord{" +
                "wordVariation='" + wordVariation + '\n' +
                ", meanings=" + meanings +
                '}';
    }
}
