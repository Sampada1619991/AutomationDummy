package com.library.borrowing.repository;

import com.library.borrowing.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for AuditLog entities.
 * Used to log all checkout attempts (success and failure) for compliance and debugging.
 */
@Repository
public interface AuditRepository extends JpaRepository<AuditLog, UUID> {
    
    /**
     * Find all audit logs for a given member within a time range.
     */
    List<AuditLog> findByMemberIdAndTimestampBetween(UUID memberId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Find all audit logs for a given book within a time range.
     */
    List<AuditLog> findByBookIdAndTimestampBetween(UUID bookId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Find audit logs by correlation ID for request tracing.
     */
    List<AuditLog> findByCorrelationId(String correlationId);
}

