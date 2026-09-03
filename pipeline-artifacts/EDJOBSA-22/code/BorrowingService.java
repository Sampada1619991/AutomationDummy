package com.library.borrowing.service;

import com.library.borrowing.dto.LoanResponse;
import com.library.borrowing.entity.AuditLog;
import com.library.borrowing.entity.Book;
import com.library.borrowing.entity.Loan;
import com.library.borrowing.entity.Member;
import com.library.borrowing.exception.*;
import com.library.borrowing.repository.AuditRepository;
import com.library.borrowing.repository.BookRepository;
import com.library.borrowing.repository.LoanRepository;
import com.library.borrowing.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for handling self-service book checkout operations.
 * 
 * This is the core business logic layer.
 * All checkout operations are wrapped in @Transactional to ensure ACID guarantees,
 * specifically to prevent double-loan scenarios (NFR-5).
 */
@Slf4j
@Service
public class BorrowingService {
    
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final AuditLogger auditLogger;
    private final Clock clock;
    
    @Value("${library.borrowing.limit:5}")
    private Integer borrowingLimit;
    
    @Value("${library.borrowing.duration-days:14}")
    private Integer loanDurationDays;

    @Autowired
    public BorrowingService(
        BookRepository bookRepository,
        MemberRepository memberRepository,
        LoanRepository loanRepository,
        AuditLogger auditLogger,
        Clock clock
    ) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
        this.auditLogger = auditLogger;
        this.clock = clock;
    }

    /**
     * Checkout a book for a member.
     * 
     * This is the main orchestration method that:
     * 1. Validates member status (no fines, within limit)
     * 2. Loads book with pessimistic lock to prevent double-checkout
     * 3. Creates a loan record
     * 4. Logs the audit entry
     * 
     * All steps happen within a single transaction for atomicity.
     * 
     * @param memberId the member ID (from JWT token)
     * @param isbn the ISBN-13 of the book
     * @param correlationId unique request correlation ID for tracing
     * @return LoanResponse with loan details
     * @throws FinesOutstandingException if member has unpaid fines
     * @throws BorrowingLimitExceededException if member has reached the limit
     * @throws BookNotFoundException if ISBN is not in the catalog
     * @throws BookUnavailableException if the book is not available (already loaned, etc)
     */
    @Transactional(rollbackFor = Exception.class)
    public LoanResponse checkoutBook(UUID memberId, String isbn, String correlationId) {
        
        log.info("Checkout attempt: memberId={}, isbn={}, correlationId={}", memberId, isbn, correlationId);
        
        try {
            // Step 1: Validate member
            Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));
            
            // Check for outstanding fines
            if (member.getOutstandingFineBalance().compareTo(BigDecimal.ZERO) > 0) {
                log.warn("Checkout denied: member has outstanding fines. memberId={}, fines={}, correlationId={}",
                    memberId, member.getOutstandingFineBalance(), correlationId);
                auditLogger.logCheckout(memberId, null, "FINES_OUTSTANDING", correlationId,
                    "Member has outstanding fines: " + member.getOutstandingFineBalance());
                throw new FinesOutstandingException(
                    "Member has outstanding fines of " + member.getOutstandingFineBalance());
            }
            
            // Check borrowing limit
            Integer activeLoanCount = loanRepository.countActiveLoansByMemberId(memberId);
            if (activeLoanCount >= borrowingLimit) {
                log.warn("Checkout denied: member has reached borrowing limit. memberId={}, activeLoans={}, limit={}, correlationId={}",
                    memberId, activeLoanCount, borrowingLimit, correlationId);
                auditLogger.logCheckout(memberId, null, "LIMIT_EXCEEDED", correlationId,
                    "Member has " + activeLoanCount + " active loans (limit: " + borrowingLimit + ")");
                throw new BorrowingLimitExceededException(
                    "Member has reached the borrowing limit of " + borrowingLimit);
            }
            
            // Step 2: Load book with pessimistic lock
            Book book = bookRepository.findByIsbnWithLock(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
            
            // Check book availability
            if (!"AVAILABLE".equals(book.getStatus())) {
                log.warn("Checkout denied: book not available. bookId={}, isbn={}, status={}, correlationId={}",
                    book.getId(), isbn, book.getStatus(), correlationId);
                auditLogger.logCheckout(memberId, book.getId(), "BOOK_UNAVAILABLE", correlationId,
                    "Book status is: " + book.getStatus());
                throw new BookUnavailableException("Book is not available. Current status: " + book.getStatus());
            }
            
            // Step 3: Create loan record
            LocalDate loanDate = LocalDate.now(clock);
            LocalDate dueDate = loanDate.plusDays(loanDurationDays);
            
            Loan loan = new Loan(memberId, book.getId(), loanDate, dueDate, isbn);
            Loan savedLoan = loanRepository.save(loan);
            
            log.info("Loan created: loanId={}, bookId={}, memberId={}, dueDate={}, correlationId={}",
                savedLoan.getId(), book.getId(), memberId, dueDate, correlationId);
            
            // Step 4: Update book status
            book.setStatus("LOANED");
            bookRepository.save(book);
            
            log.info("Book status updated to LOANED: bookId={}, isbn={}, correlationId={}",
                book.getId(), isbn, correlationId);
            
            // Step 5: Log audit entry (SYNCHRONOUS - inside same transaction)
            auditLogger.logCheckout(memberId, book.getId(), "SUCCESS", correlationId);
            
            log.info("Checkout successful: loanId={}, bookId={}, memberId={}, correlationId={}",
                savedLoan.getId(), book.getId(), memberId, correlationId);
            
            // Return response
            return new LoanResponse(
                savedLoan.getId(),
                isbn,
                memberId,
                dueDate,
                loanDate
            );
            
        } catch (BorrowingException e) {
            // Expected business exceptions - already logged
            throw e;
        } catch (Exception e) {
            // Unexpected exceptions
            log.error("Unexpected error during checkout: memberId={}, isbn={}, correlationId={}, error={}",
                memberId, isbn, correlationId, e.getMessage(), e);
            auditLogger.logCheckout(memberId, null, "ERROR", correlationId, e.getMessage());
            throw e;
        }
    }
}

