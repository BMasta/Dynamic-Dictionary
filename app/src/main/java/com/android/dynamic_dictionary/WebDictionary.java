package com.android.dynamic_dictionary;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class WebDictionary {
    public enum Responses {
        SUCCESS,
        NO_DATA,
        RESPONSE_ERROR,
        NO_RESPONSE
    }

    public static void updateDefinitions(Context context, List<WordEntry> words) {
        for (int i = 0; i < words.size(); ++i) {
            if (words.get(i).getWordsJson() == null)
                sendDefinitionRequest(context, words.get(i).getWord(), null);
        }
    }

    public static void sendDefinitionRequest(Context context, String word, VariationsPagerAdapter.ViewPagerViewHolder holder) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> ((WebDictionaryResponseListener) context).handleResponseData(word, response, Responses.SUCCESS, holder),
                volleyError -> {
                    if (volleyError.networkResponse != null) {
                        if (volleyError.networkResponse.statusCode != 404) {
                            ((WebDictionaryResponseListener) context).handleResponseData(word, null, Responses.RESPONSE_ERROR, holder);
                        } else {
                            ((WebDictionaryResponseListener) context).handleResponseData(word, null, Responses.NO_DATA, holder);
                        }
                    } else {
                        ((WebDictionaryResponseListener) context).handleResponseData(word, null, Responses.NO_RESPONSE, holder);
                    }
                });
        queue.add(stringRequest);
    }

    interface WebDictionaryResponseListener {
        void handleResponseData(String word, String responseData, Responses responseType, VariationsPagerAdapter.ViewPagerViewHolder holder);
    }
}
