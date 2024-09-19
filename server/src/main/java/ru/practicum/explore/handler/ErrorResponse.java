package ru.practicum.explore.handler;

import lombok.Getter;

import java.util.List;

@Getter
class ErrorResponse {
    private final String message;
    private final List<String> errors;
    private final String reason;
    private final String status;
    private final String timestamp;

    public ErrorResponse(String message, List<String> errors, String reason, String status, String timestamp) {
        this.message = message;
        this.errors = errors;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
    }
}