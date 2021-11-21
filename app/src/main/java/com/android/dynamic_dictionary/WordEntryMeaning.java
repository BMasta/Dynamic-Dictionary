package com.android.dynamic_dictionary;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WordEntryMeaning {
    //--------------------------fields--------------------------//
    private String partOfSpeech;
    private final JSONObject meaningJson;
    private List<WordEntryDefinition> definitions;

    //-----------------------constructor------------------------//
    public WordEntryMeaning(JSONObject meaningJson) {
        this.meaningJson = meaningJson;
        getPartOfSpeech();
        getDefinitions();
    }

    //-------------------getters and setters--------------------//
    public String getPartOfSpeech() {
        if (partOfSpeech == null) {
            try {
                partOfSpeech = meaningJson.getString("partOfSpeech");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return partOfSpeech;
    }

    public List<WordEntryDefinition> getDefinitions() {
        if (definitions == null) {
            try {
                definitions = new ArrayList<>();
                JSONArray defs = meaningJson.getJSONArray("definitions");
                for (int k = 0; k < defs.length(); ++k) {
                    definitions.add(new WordEntryDefinition(defs.getJSONObject(k)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return definitions;
    }

    //-------------------add more definitions-------------------//
    public void addDefinitions(List<WordEntryDefinition> definitionsToAdd) {
        definitions.addAll(definitionsToAdd);

    }

    //-------------------------toString-------------------------//
    @NonNull
    @Override
    public String toString() {
        getPartOfSpeech();
        getDefinitions();
        return "WordEntryMeaning{" +
                "partOfSpeech='" + partOfSpeech + '\n' +
                ", definitions=" + definitions +
                '}';
    }
}
