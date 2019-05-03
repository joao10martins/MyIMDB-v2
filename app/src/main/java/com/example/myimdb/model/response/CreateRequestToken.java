package com.example.myimdb.model.response;

public class CreateRequestToken {

    private boolean success;
    private String request_token;


    public CreateRequestToken(boolean success, String request_token) {
        this.success = success;
        this.request_token = request_token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getRequest_token() {
        return request_token;
    }

    public void setRequest_token(String request_token) {
        this.request_token = request_token;
    }
}
