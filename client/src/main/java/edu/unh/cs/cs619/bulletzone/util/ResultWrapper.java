package edu.unh.cs.cs619.bulletzone.util;

public class ResultWrapper {
    private boolean success;
    private String message;
    private Long userId;

    public ResultWrapper() {
        // Default constructor
    }

    public ResultWrapper(boolean success, String message, Long userId) {
        this.success = success;
        this.message = message;
        this.userId = userId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}