package com.begin.todo_api.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                LocalDateTime.now(),
                errors
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleStatus(ResponseStatusException ex) {
        ApiError body = new ApiError(
                ex.getStatusCode().value(),
                ex.getReason(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex) {
        ApiError body = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error",
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
