package com.library.borrowing.exception;

/**
 * Thrown when a member has outstanding fines and cannot checkout.
 */
public class FinesOutstandingException extends BorrowingException {
    
    public FinesOutstandingException(String message) {
        super("FINES_OUTSTANDING", message);
    }
}

