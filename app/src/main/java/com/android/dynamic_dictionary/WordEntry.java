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
    private List<WordEntryVariation> wordVariations;
    //-----------------------constructor------------------------//
    public WordEntry(String word, String description, String wordsJson) {
        super();
        this.word = word;
        this.description = description;
        this.wordsJson = wordsJson;
        getWordVariations();
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

    public List<WordEntryVariation> getWordVariations() {
        if (wordVariations == null) {
            wordVariations = new ArrayList<>();
            // no json data = no dictionary info
            if (wordsJson.equals(""))
                return wordVariations;

            try {
                JSONArray wordsParsed = new JSONArray(wordsJson);
                for (int iWordsParsed = 0, iWordVar = 0; iWordsParsed < wordsParsed.length(); ++iWordsParsed) {
                    WordEntryVariation newVar = new WordEntryVariation(wordsParsed.getJSONObject(iWordsParsed));
                    if (iWordVar > 0 && newVar.getWordVariation().equals(wordVariations.get(iWordVar-1).getWordVariation())) {
                        // combine meanings of 2 or more variations if they have the same title
                        wordVariations.get(iWordVar-1).addMeanings(newVar.getMeanings());
                    } else {
                        iWordVar++;
                        wordVariations.add(newVar);
                    }
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
