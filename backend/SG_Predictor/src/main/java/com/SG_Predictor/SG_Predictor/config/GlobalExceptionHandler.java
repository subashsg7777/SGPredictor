package com.SG_Predictor.SG_Predictor.config;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("[EXCEPTION] ConstraintViolationException: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Validation failed: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error("[EXCEPTION] MethodArgumentNotValidException: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Invalid request body: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex) {
        log.error("[EXCEPTION] MissingServletRequestParameterException: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Missing parameter: " + ex.getParameterName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("[EXCEPTION] MethodArgumentTypeMismatchException: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Invalid parameter type: " + ex.getName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("[EXCEPTION] Unhandled exception: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Internal server error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }
}
