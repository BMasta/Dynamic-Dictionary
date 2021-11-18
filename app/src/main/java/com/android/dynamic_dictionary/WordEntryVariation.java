package com.android.dynamic_dictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WordEntryVariation {
    //--------------------------fields--------------------------//
    private String wordVariation;
    private JSONObject wordJson;
    private List<WordEntryMeaning> meanings;
    //-----------------------constructor------------------------//
    public WordEntryVariation(JSONObject wordJson) {
        this.wordJson = wordJson;
        getWordVariation();
        getMeanings();
    }
    //-------------------getters and setters--------------------//
    public String getWordVariation() {
        if (wordVariation == null) {
            try {
                wordVariation =  wordJson.getString("word");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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
    //---------------------add more meanings--------------------//
    public void addMeanings(List<WordEntryMeaning> meaningsToAdd) {
        for (int iMeanings = 0, iMeaningsToAdd = 0; iMeaningsToAdd < meaningsToAdd.size(); ++iMeaningsToAdd) {
            WordEntryMeaning newMeaning = meaningsToAdd.get(iMeaningsToAdd);
            if (newMeaning.getPartOfSpeech().equals(meanings.get(iMeanings).getPartOfSpeech())) {
                meanings.get(iMeanings).addDefinitions(newMeaning.getDefinitions());
            } else {
                iMeanings++;
                meanings.add(newMeaning);
            }
        }
    }
    //-------------------------toString-------------------------//
    @Override
    public String toString() {
        getMeanings();
        return "WordEntryVariation{" +
                "wordVariation='" + wordVariation + '\n' +
                ", meanings=" + meanings +
                '}';
    }
}
