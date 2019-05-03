package com.example.myimdb.model.response;

public class ValidateRequestToken {

    private boolean success;
    private String request_token;
    private int status_code;
    private String status_message;



    public ValidateRequestToken(boolean success, String request_token, int status_code, String status_message) {
        this.success = success;
        this.request_token = request_token;
        this.status_code = status_code;
        this.status_message = status_message;
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

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }
}
