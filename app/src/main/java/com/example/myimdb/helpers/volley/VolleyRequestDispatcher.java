package com.example.myimdb.helpers.volley;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by btrincao on 12/02/2018.
 */

public class VolleyRequestDispatcher {

    public static<T> void doNetworkOperation(VolleyRequest request, final RequestResponseListener.Listener listener,
                                             final RequestResponseListener.ErrorListener errorListener,
                                             Class<T> clazz) {
        try {
            Map<String, String> headers = new HashMap<>();
            if (request.contentType != null) {
                headers.put("Content-Type", request.contentType);

                if(request.authHeaders != null && request.authHeaders.size() > 0){
                    for (Map.Entry<String, String> entry : request.authHeaders.entrySet())
                    {
                        headers.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            GsonRequestT<T> gsonRequestT = new GsonRequestT<>(request.method, request.url, clazz, headers,
                    response -> {
                        try {

                            listener.onResponse(response);
                        } catch (Exception e) {
                            Log.e("RequestDispatcher", e.getMessage());
                        }
                    }, error -> {
                        try {
                            errorListener.onError(new NetworkException(error.getMessage()));
                        } catch (Exception e) {
                            Log.e("RequestDispatcher", e.getMessage());
                        }
                    });

            gsonRequestT.setRetryPolicy(new DefaultRetryPolicy(0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleyLog.e("Request url %s", request.url);
            if (request.method == Request.Method.POST || request.method == Request.Method.DELETE) {
                gsonRequestT.setRequestBody(request.data);
                if (request.data != null) {
                    VolleyLog.e("Request body %s", request.data);
                }
            }
            if (request.tag != null) {
                gsonRequestT.setTag(request.tag);
            }
            VolleyRequestQueue.getInstance(request.context).addToRequestQueue(gsonRequestT);
            VolleyLog.e("Request time", "Request");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
