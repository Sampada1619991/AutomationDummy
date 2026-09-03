package com.library.borrowing.exception;

/**
 * Base exception for borrowing domain errors.
 */
public class BorrowingException extends RuntimeException {
    
    private final String errorCode;

    public BorrowingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BorrowingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

