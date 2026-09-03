package com.library.borrowing.exception;

/**
 * Thrown when a book is not found by ISBN.
 */
public class BookNotFoundException extends BorrowingException {
    
    public BookNotFoundException(String message) {
        super("BOOK_NOT_FOUND", message);
    }
}

