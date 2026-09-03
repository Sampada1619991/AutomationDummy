package com.library.borrowing.repository;

import com.library.borrowing.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Loan entities.
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    
    /**
     * Count the number of active loans for a given member.
     */
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.memberId = :memberId AND l.status = 'ACTIVE'")
    Integer countActiveLoansByMemberId(@Param("memberId") UUID memberId);
    
    /**
     * Find all active loans for a given member.
     */
    @Query("SELECT l FROM Loan l WHERE l.memberId = :memberId AND l.status = 'ACTIVE' ORDER BY l.dueDate ASC")
    List<Loan> findActiveLoansByMemberId(@Param("memberId") UUID memberId);
    
    /**
     * Find all loans for a given book.
     */
    List<Loan> findByBookId(UUID bookId);
}

