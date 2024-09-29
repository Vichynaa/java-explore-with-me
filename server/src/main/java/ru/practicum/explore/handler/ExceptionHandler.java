package ru.practicum.explore.handler;

import exception.ApiError;
import exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleApiOverflow(final ApiError e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getErrors(), e.getReason(), e.getStatus(), e.getTimestamp());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getStatus()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNumberFormatOverflow(final NumberFormatException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), List.of(Arrays.toString(e.getStackTrace()).split("(?<=\\)), ")), "Incorrectly made request.", "BAD_REQUEST", LocalDateTime.now().format(formatter));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleValidationOverflow(final ValidationException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), List.of(Arrays.toString(e.getStackTrace()).split("(?<=\\)), ")), "Main server error.", "BAD_REQUEST", LocalDateTime.now().format(formatter));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handlePropertyValueOverflow(final org.hibernate.PropertyValueException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), List.of(Arrays.toString(e.getStackTrace()).split("(?<=\\)), ")), "Main server error.", "BAD_REQUEST", LocalDateTime.now().format(formatter));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handlerPSQLEOverflow(final org.postgresql.util.PSQLException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), List.of(Arrays.toString(e.getStackTrace()).split("(?<=\\)), ")), "Main server error.", "BAD_REQUEST", LocalDateTime.now().format(formatter));
        if (e.getMessage().contains("ERROR: duplicate key value violates unique constraint")) {
            errorResponse = new ErrorResponse(e.getMessage(), List.of(Arrays.toString(e.getStackTrace()).split("(?<=\\)), ")), "Main server error.", "CONFLICT", LocalDateTime.now().format(formatter));
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
    }
}
