package com.library.borrowing.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Book entity representing a library book with its current availability status.
 */
@Entity
@Table(name = "book", 
       indexes = {
           @Index(name = "idx_book_isbn", columnList = "isbn"),
           @Index(name = "idx_book_status", columnList = "status")
       })
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "isbn", nullable = false, unique = true, length = 20)
    private String isbn;
    
    @Column(name = "title", nullable = false, length = 500)
    private String title;
    
    @Column(name = "author", length = 500)
    private String author;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status; // AVAILABLE, LOANED, LOST, RESERVED
    
    @Column(name = "catalog_price", precision = 10, scale = 2)
    private BigDecimal catalogPrice;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }

    public Book() {
    }

    public Book(String isbn, String title, String author, String status) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getCatalogPrice() {
        return catalogPrice;
    }

    public void setCatalogPrice(BigDecimal catalogPrice) {
        this.catalogPrice = catalogPrice;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

