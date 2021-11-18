package com.android.dynamic_dictionary;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Header;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class WebDictionary {
    private WebDictionaryResponseListener listener;
    private static boolean isPreviousResponseSuccessful = true;
    private static WebDictionary instance;

    public static WebDictionary getInstance(Context context) {
        if (instance == null) {
            instance = new WebDictionary(context);
        }

        return instance;
    }

    private WebDictionary(Context context) {
        try {
            listener = (WebDictionaryResponseListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement WebDictionaryResponseListener");
        }
    }

    public static void sendMultipleDefinitionRequests(Context context, List<WordEntry> words) {
        for (int i = 0; i < words.size(); ++i) {
            sendDefinitionRequest(context, words.get(i).getWord());
        }
    }

    public static void sendDefinitionRequest(Context context, String word){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    getInstance(context).listener.handleResponseData(word, response);
                }
            },
            new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.networkResponse != null) {
                    if (volleyError.networkResponse.statusCode != 404) {
                        Toast.makeText(context, "Dictionary response error (" + volleyError.networkResponse.statusCode + ").", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "Couldn't find " + word + " in the dictionary.", Toast.LENGTH_SHORT).show();
                    }
                } else if (isPreviousResponseSuccessful){
                    isPreviousResponseSuccessful = false;
                    Toast.makeText(context, "No response from the dictionary. Check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        queue.add(stringRequest);
    }

    interface WebDictionaryResponseListener {
        void handleResponseData(String word, String response);
    }
}
