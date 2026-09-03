package com.library.borrowing.controller;

import com.library.borrowing.dto.ErrorResponse;
import com.library.borrowing.exception.BorrowingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler for the borrowing API.
 * 
 * Maps domain exceptions to HTTP status codes and error response bodies.
 */
@Slf4j
@ControllerAdvice
public class BorrowingExceptionHandler {

    /**
     * Handle domain borrowing exceptions (fines, limit, unavailable, not found, etc).
     * 
     * All borrowing exceptions map to HTTP 409 Conflict because they represent
     * business rule violations, not resource not found or bad request.
     */
    @ExceptionHandler(BorrowingException.class)
    public ResponseEntity<ErrorResponse> handleBorrowingException(
            BorrowingException ex,
            WebRequest request) {
        
        log.warn("Borrowing error: code={}, message={}", ex.getErrorCode(), ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        
        return ResponseEntity
            .status(HttpStatus.CONFLICT) // HTTP 409
            .body(errorResponse);
    }

    /**
     * Handle validation errors (invalid request payload).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        log.warn("Validation error: {}", ex.getBindingResult());
        
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .reduce((a, b) -> a + "; " + b)
            .orElse("Invalid request body");
        
        ErrorResponse errorResponse = new ErrorResponse("INVALID_REQUEST", message);
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST) // HTTP 400
            .body(errorResponse);
    }

    /**
     * Handle access denied (missing OAuth2 scope).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request) {
        
        log.warn("Access denied: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse("ACCESS_DENIED", 
            "You do not have permission to access this resource. Required scope: library:borrow");
        
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN) // HTTP 403
            .body(errorResponse);
    }

    /**
     * Catch-all for unexpected errors.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request) {
        
        log.error("Unexpected error", ex);
        
        ErrorResponse errorResponse = new ErrorResponse("INTERNAL_ERROR", 
            "An unexpected error occurred. Please contact support.");
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 500
            .body(errorResponse);
    }
}

