package com.library.borrowing.exception;

/**
 * Thrown when a member has reached their borrowing limit.
 */
public class BorrowingLimitExceededException extends BorrowingException {
    
    public BorrowingLimitExceededException(String message) {
        super("LIMIT_EXCEEDED", message);
    }
}

