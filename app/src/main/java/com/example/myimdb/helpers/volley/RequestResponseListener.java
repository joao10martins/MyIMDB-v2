package com.example.myimdb.helpers.volley;

/**
 * Created by btrincao on 12/02/2018.
 */

public class RequestResponseListener {

    public interface Listener{
        <T> void onResponse(T response);
    }


    public interface ErrorListener{
        void onError(NetworkException error);
    }

    public interface AuthErrorListener{
        void onAuthError();
    }
}
