package com.library.borrowing.exception;

/**
 * Thrown when a book is not available for checkout (already loaned, lost, etc).
 */
public class BookUnavailableException extends BorrowingException {
    
    public BookUnavailableException(String message) {
        super("BOOK_UNAVAILABLE", message);
    }
}

