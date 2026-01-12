package com.gardey.tennis_sheet.exceptions;

import java.time.Instant;

/**
 * Simple DTO to return error information from controllers.
 */
public class ErrorResponse {
    private long timestamp;
    private int status;
    private String message;

    public ErrorResponse() {
        this.timestamp = Instant.now().toEpochMilli();
    }

    public ErrorResponse(String message, int status) {
        this.timestamp = Instant.now().toEpochMilli();
        this.message = message;
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}