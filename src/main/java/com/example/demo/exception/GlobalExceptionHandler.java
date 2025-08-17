package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * 
 * This class handles exceptions thrown by controllers and converts them
 * to appropriate HTTP status codes and response bodies.
 * 
 * Key Concepts:
 * - @ControllerAdvice: Makes this class a global exception handler
 * - @ExceptionHandler: Specifies which exception types to handle
 * - ResponseEntity: Returns structured HTTP responses
 * - Error Messages: Provides meaningful error information
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors from @Valid annotations
     * 
     * @param ex The validation exception
     * @return ResponseEntity with 400 Bad Request status and validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handle JSON parsing errors
     * 
     * @param ex The JSON parsing exception
     * @return ResponseEntity with 400 Bad Request status
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid JSON format: " + ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle RuntimeException for duplicate email
     * 
     * @param ex The runtime exception
     * @return ResponseEntity with 409 Conflict status for duplicate email
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        
        if (ex.getMessage().contains("already exists")) {
            error.put("error", "User with this email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } else if (ex.getMessage().contains("not found")) {
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } else if (ex.getMessage().contains("Database connection failed")) {
            error.put("error", "Database connection failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } else {
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Handle IllegalArgumentException for validation errors
     * 
     * @param ex The illegal argument exception
     * @return ResponseEntity with 400 Bad Request status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle generic exceptions
     * 
     * @param ex The exception
     * @return ResponseEntity with 500 Internal Server Error status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
