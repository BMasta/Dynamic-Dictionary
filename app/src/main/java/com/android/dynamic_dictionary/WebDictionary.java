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

    public static void sendMultipleDefinitionRequests(Context context, List<WordEntry> words) {
        for (int i = 0; i < words.size(); ++i) {
            sendDefinitionRequest(context, words.get(i).getWord());
        }
    }

    public enum Responses {
        SUCCESS,
        NO_DATA,
        RESPONSE_ERROR,
        NO_RESPONSE
    }

    public static void sendDefinitionRequest(Context context, String word){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    ((WebDictionaryResponseListener)context).handleResponseData(word, response, Responses.SUCCESS);
                }
            },
            new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.networkResponse != null) {
                    if (volleyError.networkResponse.statusCode != 404) {
                        ((WebDictionaryResponseListener)context).handleResponseData(word, null, Responses.RESPONSE_ERROR);
                    }
                    else {
                        ((WebDictionaryResponseListener)context).handleResponseData(word, null, Responses.NO_DATA);
                    }
                } else if (isPreviousResponseSuccessful){
                    ((WebDictionaryResponseListener)context).handleResponseData(word, null, Responses.NO_RESPONSE);
                }
            }
        });
        queue.add(stringRequest);
    }

    interface WebDictionaryResponseListener {
        void handleResponseData(String word, String responseData, Responses responseType);
    }
}
