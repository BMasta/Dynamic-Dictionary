package com.android.dynamic_dictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WordEntryDefinition {
    private String title;
    private String example;
    private JSONObject definitionJson;
    private List<String> synonyms;

    public WordEntryDefinition(JSONObject definitionJson, boolean parseNow) {
        this.definitionJson = definitionJson;
        if (parseNow) {
            getTitle();
            getExample();
            getSynonyms();
        }
    }

    public String getTitle() {
        if (title == null) {
            try {
                title = definitionJson.getString("definition");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return title;
    }

    public String getExample() {
        if (example == null) {
            try {
                example = definitionJson.getString("example");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return example;
    }

    public JSONObject getDefinitionJson() {
        return definitionJson;
    }

    public List<String> getSynonyms() {
        if (synonyms == null) {
            try {
                synonyms = new ArrayList<>();
                JSONArray syns = definitionJson.getJSONArray("synonyms");
                for (int k = 0; k < syns.length(); ++k) {
                    synonyms.add(syns.getString(k));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return synonyms;
    }

    @Override
    public String toString() {
        return "WordEntryDefinition{" +
                "title='" + title + '\'' +
                ", example='" + example + '\'' +
                ", synonyms=" + synonyms +
                '}';
    }
}
