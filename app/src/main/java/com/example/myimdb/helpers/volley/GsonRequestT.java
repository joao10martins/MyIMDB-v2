package com.example.myimdb.helpers.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by btrincao on 12/02/2018.
 */

public class GsonRequestT<T> extends Request<T> {

    private static final String TAG = "GsonRequestT";

    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;

    private String mRequestBody;


    public GsonRequestT(int method, String url, Class<T> clazz, Map<String, String> headers,
                        Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
    }

    void setRequestBody(String val){
        this.mRequestBody = val;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }


    @Override
    public byte[] getBody(){
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, "utf-8");
            return null;
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            VolleyLog.e("Network response waiting", "");
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            VolleyLog.e("Network response : %s", json);
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
//            VolleyLog.e("Network response UnsupportedEncodingException", e.getMessage());
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
//            VolleyLog.e("Network response JsonSyntaxException", e.getMessage());
            return Response.error(new ParseError(e));
        } catch (Exception e) {
//            VolleyLog.e("Network response exception", e.getMessage());
            return Response.error(new ParseError(e));
        }

    }
}
