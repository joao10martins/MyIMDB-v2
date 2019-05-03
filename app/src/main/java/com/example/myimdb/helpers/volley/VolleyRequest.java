package com.example.myimdb.helpers.volley;

import android.content.Context;

import java.util.Map;

/**
 * Created by btrincao on 12/02/2018.
 */

public class VolleyRequest {

    public int method;
    public String url;
    public String data;
    public String contentType;
    public Context context;
    public String tag;
    public Map<String, String> authHeaders;
}
