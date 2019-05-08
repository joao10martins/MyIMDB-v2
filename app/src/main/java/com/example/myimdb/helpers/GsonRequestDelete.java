package com.example.myimdb.helpers;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class GsonRequestDelete<T> extends Request<T> {
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String,String> headers;
    private final Response.Listener<T> listener;
    private final Response.ErrorListener errorListener;

    private String mRequestBody;

    /**
     * Make a POST request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     */
    public GsonRequestDelete(String url,
                           Class<T> clazz,
                           Map<String,String> headers,
                           Response.Listener<T> listener,
                           Response.ErrorListener errorListener) {
        super(Method.DELETE, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.errorListener = errorListener;
    }



    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    void setRequestBody(String val){
        this.mRequestBody = val;
    }


    @Override
    public byte[] getBody() throws AuthFailureError {

        return gson.toJson(headers).getBytes(StandardCharsets.UTF_8);
        /*try {
            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, "utf-8");
            return null;
        }*/
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }


    /*@Override
    public void deliverError(VolleyError error) {
        errorListener.onErrorResponse(error);
    }*/

    /*@Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        //return super.parseNetworkError(volleyError);
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }

        return volleyError;
    }*/

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
