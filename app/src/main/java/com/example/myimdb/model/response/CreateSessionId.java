package com.example.myimdb.model.response;

public class CreateSessionId {

    private boolean success;
    private boolean failure;
    private int status_code;
    private String status_message;
    private String session_id;


    public CreateSessionId(boolean success, boolean failure, int status_code, String status_message, String session_id) {
        this.success = success;
        this.failure = failure;
        this.status_code = status_code;
        this.status_message = status_message;
        this.session_id = session_id;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isFailure() {
        return failure;
    }

    public void setFailure(boolean failure) {
        this.failure = failure;
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

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }
}
