package com.example.myimdb.helpers.volley;

/**
 * Created by btrincao on 12/02/2018.
 */

public interface NetworkResponse {

    interface Listener{
        void onResponse(Object result);
    }
    interface ErrorListener{
        void onError(NetworkException error);
    }
}
