package com.yusolbin.bio_os.dto;

public class AdminInsightResponse {

    private String severity;
    private String message;

    public AdminInsightResponse(String severity, String message) {
        this.severity = severity;
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }
}