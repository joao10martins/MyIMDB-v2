package com.example.myimdb.helpers;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class GsonRequestPost<T> extends Request<T> {
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final JSONObject data;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     */


    public GsonRequestPost(String url,
                       Class<T> clazz,
                       JSONObject data,
                       Map<String, String> headers,
                       Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.clazz = clazz;
        this.data = data;
        this.headers = headers;
        this.listener = listener;
    }


    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return gson.toJson(data).getBytes();
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
