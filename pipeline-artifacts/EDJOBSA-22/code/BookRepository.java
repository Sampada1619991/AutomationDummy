package com.library.borrowing.repository;

import com.library.borrowing.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Book entities.
 * Provides pessimistic locking to ensure exactly-one-winner checkout on concurrent attempts.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    
    /**
     * Find a book by ISBN with pessimistic write lock.
     * This lock ensures that only one transaction can modify the book at a time,
     * preventing double-loan scenarios.
     * 
     * @param isbn the ISBN-13
     * @return Optional containing the book if found
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Book b WHERE b.isbn = :isbn")
    Optional<Book> findByIsbnWithLock(@Param("isbn") String isbn);
    
    /**
     * Find a book by ISBN without locking (for read-only operations).
     */
    Optional<Book> findByIsbn(String isbn);
}

