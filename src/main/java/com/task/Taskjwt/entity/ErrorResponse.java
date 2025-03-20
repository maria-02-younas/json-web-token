package com.task.Taskjwt.entity;

public class ErrorResponse {
    private String statusCode;
    private String message;

    public ErrorResponse(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
