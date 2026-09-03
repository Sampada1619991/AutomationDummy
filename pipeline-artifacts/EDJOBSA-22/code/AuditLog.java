package com.library.borrowing.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Immutable audit log entry for checkout attempts (both success and failure).
 * Used to track all borrowing activity for compliance and debugging.
 */
@Entity
@Table(name = "audit_log", 
       indexes = {
           @Index(name = "idx_audit_member_timestamp", columnList = "member_id, timestamp"),
           @Index(name = "idx_audit_book_timestamp", columnList = "book_id, timestamp"),
           @Index(name = "idx_audit_correlation_id", columnList = "correlation_id")
       })
@Immutable
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "member_id", nullable = false)
    private UUID memberId;
    
    @Column(name = "book_id", nullable = false)
    private UUID bookId;
    
    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime timestamp;
    
    @Column(name = "outcome", nullable = false, length = 50)
    private String outcome; // e.g., "SUCCESS", "FINES_OUTSTANDING", "LIMIT_EXCEEDED", "BOOK_UNAVAILABLE", "BOOK_NOT_FOUND"
    
    @Column(name = "correlation_id", nullable = false, length = 36)
    private String correlationId; // UUID for request tracing
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message; // Optional error message or details

    public AuditLog() {
    }

    public AuditLog(UUID memberId, UUID bookId, LocalDateTime timestamp, String outcome, String correlationId) {
        this.memberId = memberId;
        this.bookId = bookId;
        this.timestamp = timestamp;
        this.outcome = outcome;
        this.correlationId = correlationId;
    }

    public AuditLog(UUID memberId, UUID bookId, LocalDateTime timestamp, String outcome, String correlationId, String message) {
        this(memberId, bookId, timestamp, outcome, correlationId);
        this.message = message;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", bookId=" + bookId +
                ", timestamp=" + timestamp +
                ", outcome='" + outcome + '\'' +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}

