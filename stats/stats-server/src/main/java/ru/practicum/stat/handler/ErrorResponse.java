package ru.practicum.stat.handler;

import lombok.Getter;

@Getter
class ErrorResponse {
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

}