package com.android.dynamic_dictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordEntryMeaning {
    private String partOfSpeech;
    private JSONObject meaningJson;
    private List<WordEntryDefinition> definitions;

    public WordEntryMeaning(JSONObject meaningJson, boolean parseNow) {
        this.meaningJson = meaningJson;
        if (parseNow) {
            getPartOfSpeech();
            getDefinitions();
        }
    }

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

    public JSONObject getMeaningJson() {
        return meaningJson;
    }

    public List<WordEntryDefinition> getDefinitions() {
        if (definitions == null) {
            try {
                definitions = new ArrayList<>();
                JSONArray defs = meaningJson.getJSONArray("definitions");
                for (int k = 0; k < defs.length(); ++k) {
                    definitions.add(new WordEntryDefinition(defs.getJSONObject(k), false));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return definitions;
    }

    @Override
    public String toString() {
        return "WordEntryMeaning{" +
                "partOfSpeech='" + partOfSpeech + '\'' +
                ", definitions=" + definitions +
                '}';
    }
}
