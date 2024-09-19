package ru.practicum.explore.handler;

import exception.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleApiOverflow(final ApiError e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getErrors(), e.getReason(), e.getStatus(), e.getTimestamp());
        log.info(e.getStatus());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getStatus()));
    }

//Перед отправкой на ревью поменять для postgres
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInternalServerOverflow(final JdbcSQLIntegrityConstraintViolationException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), List.of(Arrays.toString(e.getStackTrace())), e.getMessage().substring(0, e.getMessage().indexOf(":")), "BAD_REQUEST", LocalDateTime.now().format(formatter));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
