package com.iulyus01.mazegenerator.api;

public class ApiJsonError {
    public int status;
    public String error;
    public String message;

    public ApiJsonError(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
