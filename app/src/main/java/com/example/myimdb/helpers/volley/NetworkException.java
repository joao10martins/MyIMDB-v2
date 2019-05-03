package com.example.myimdb.helpers.volley;

/**
 * Created by btrincao on 12/02/2018.
 */

public class NetworkException extends Exception{

    public String errorCode;

    public NetworkException(){

    }

    public NetworkException(String errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public NetworkException(String message){
        super(message);
    }
}
