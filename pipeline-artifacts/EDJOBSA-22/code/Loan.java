package com.library.borrowing.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Loan entity representing a member's current or historical book loan.
 */
@Entity
@Table(name = "loan", 
       indexes = {
           @Index(name = "idx_loan_member_id", columnList = "member_id"),
           @Index(name = "idx_loan_book_id", columnList = "book_id"),
           @Index(name = "idx_loan_status", columnList = "status")
       })
public class Loan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "member_id", nullable = false)
    private UUID memberId;
    
    @Column(name = "book_id", nullable = false)
    private UUID bookId;
    
    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "return_date")
    private LocalDate returnDate; // Null if not yet returned
    
    @Column(name = "status", nullable = false, length = 20)
    private String status; // ACTIVE, RETURNED, OVERDUE
    
    @Column(name = "isbn", nullable = false, length = 20)
    private String isbn;

    public Loan() {
    }

    public Loan(UUID memberId, UUID bookId, LocalDate loanDate, LocalDate dueDate, String isbn) {
        this.memberId = memberId;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.isbn = isbn;
        this.status = "ACTIVE";
    }

    public UUID getId() {
        return id;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public UUID getBookId() {
        return bookId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsbn() {
        return isbn;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", bookId=" + bookId +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", status='" + status + '\'' +
                '}';
    }
}

