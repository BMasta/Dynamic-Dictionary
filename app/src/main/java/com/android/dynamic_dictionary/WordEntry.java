package com.android.dynamic_dictionary;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class WordEntry {
    //--------------------------fields--------------------------//
    private String word;
    private String description;
    private String wordsJson;
    private List<WordEntryWord> wordVariations;
    //-----------------------constructor------------------------//
    public WordEntry(String word, String description, String wordsJson) {
        super();
        this.word = word;
        this.description = description;
        this.wordsJson = wordsJson;
    }
    //-------------------getters and setters--------------------//
    public String getWord() {
        return word;
    }

    public String getDescription() {
        return description;
    }

    public String getWordsJson() {
        return wordsJson;
    }

    public List<WordEntryWord> getWordVariations() {
        if (wordVariations == null) {
            wordVariations = new ArrayList<>();
            // no json data = no dictionary info
            if (wordsJson.equals(""))
                return wordVariations;

            try {
                JSONArray wordsParsed = new JSONArray(wordsJson);
                for (int i = 0; i < wordsParsed.length(); ++i) {
                    wordVariations.add(new WordEntryWord(word, wordsParsed.getJSONObject(i)));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) { }
        }

        return wordVariations;
    }
    //-------------------------toString-------------------------//
    @Override
    public String toString() {
        getWordVariations();
        return "WordEntry{" +
                "word='" + word + '\n' +
                ", description='" + description + '\n' +
                ", word variations=" + wordVariations +
                '}';
    }

    //-----------------------------------------------------------//
}
