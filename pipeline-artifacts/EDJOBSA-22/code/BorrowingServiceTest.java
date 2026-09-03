package com.library.borrowing.service;

import com.library.borrowing.dto.LoanResponse;
import com.library.borrowing.entity.Book;
import com.library.borrowing.entity.Loan;
import com.library.borrowing.entity.Member;
import com.library.borrowing.exception.*;
import com.library.borrowing.repository.BookRepository;
import com.library.borrowing.repository.LoanRepository;
import com.library.borrowing.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BorrowingService.
 * Tests the core business logic of the checkout process.
 */
@DisplayName("BorrowingService Unit Tests")
class BorrowingServiceTest {
    
    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private MemberRepository memberRepository;
    
    @Mock
    private LoanRepository loanRepository;
    
    @Mock
    private AuditLogger auditLogger;
    
    @Mock
    private Clock clock;
    
    @InjectMocks
    private BorrowingService borrowingService;
    
    private UUID memberId;
    private UUID bookId;
    private String isbn;
    private Member testMember;
    private Book testBook;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        memberId = UUID.randomUUID();
        bookId = UUID.randomUUID();
        isbn = "978-0-13-110362-7";
        
        testMember = new Member("test@library.com", "Test User");
        testMember.setOutstandingFineBalance(BigDecimal.ZERO);
        testMember.setActiveLoanCount(2);
        
        testBook = new Book(isbn, "Test Book", "Test Author", "AVAILABLE");
    }
    
    @Test
    @DisplayName("Successful checkout creates loan and updates book status")
    void testSuccessfulCheckout() {
        // Arrange
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(loanRepository.countActiveLoansByMemberId(memberId)).thenReturn(2);
        when(bookRepository.findByIsbnWithLock(isbn)).thenReturn(Optional.of(testBook));
        
        Loan savedLoan = new Loan(memberId, bookId, LocalDate.now(), LocalDate.now().plusDays(14), isbn);
        savedLoan.getId(); // Simulate saved with ID
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        
        // Act
        LoanResponse response = borrowingService.checkoutBook(memberId, isbn, "test-correlation-id");
        
        // Assert
        assertNotNull(response);
        assertEquals(isbn, response.getIsbn());
        assertEquals(memberId, response.getMemberId());
        verify(loanRepository).save(any(Loan.class));
        verify(bookRepository).save(testBook);
        verify(auditLogger).logCheckout(eq(memberId), eq(bookId), eq("SUCCESS"), eq("test-correlation-id"));
    }
    
    @Test
    @DisplayName("Checkout fails when member has outstanding fines")
    void testCheckoutFailsWithFines() {
        // Arrange
        testMember.setOutstandingFineBalance(new BigDecimal("15.50"));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        
        // Act & Assert
        assertThrows(FinesOutstandingException.class, 
            () -> borrowingService.checkoutBook(memberId, isbn, "test-correlation-id"));
        verify(auditLogger).logCheckout(eq(memberId), isNull(), eq("FINES_OUTSTANDING"), 
            eq("test-correlation-id"), contains("outstanding fines"));
    }
    
    @Test
    @DisplayName("Checkout fails when member reached borrowing limit")
    void testCheckoutFailsWithLimitExceeded() {
        // Arrange
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(loanRepository.countActiveLoansByMemberId(memberId)).thenReturn(5); // Limit is 5
        
        // Act & Assert
        assertThrows(BorrowingLimitExceededException.class, 
            () -> borrowingService.checkoutBook(memberId, isbn, "test-correlation-id"));
        verify(auditLogger).logCheckout(eq(memberId), isNull(), eq("LIMIT_EXCEEDED"), 
            eq("test-correlation-id"), contains("borrowing limit"));
    }
    
    @Test
    @DisplayName("Checkout fails when book not found")
    void testCheckoutFailsWithBookNotFound() {
        // Arrange
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(loanRepository.countActiveLoansByMemberId(memberId)).thenReturn(2);
        when(bookRepository.findByIsbnWithLock(isbn)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(BookNotFoundException.class, 
            () -> borrowingService.checkoutBook(memberId, isbn, "test-correlation-id"));
        verify(auditLogger).logCheckout(eq(memberId), isNull(), eq("BOOK_NOT_FOUND"), 
            eq("test-correlation-id"), contains(isbn));
    }
    
    @Test
    @DisplayName("Checkout fails when book not available")
    void testCheckoutFailsWithBookUnavailable() {
        // Arrange
        testBook.setStatus("LOANED");
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(loanRepository.countActiveLoansByMemberId(memberId)).thenReturn(2);
        when(bookRepository.findByIsbnWithLock(isbn)).thenReturn(Optional.of(testBook));
        
        // Act & Assert
        assertThrows(BookUnavailableException.class, 
            () -> borrowingService.checkoutBook(memberId, isbn, "test-correlation-id"));
        verify(auditLogger).logCheckout(eq(memberId), any(UUID.class), eq("BOOK_UNAVAILABLE"), 
            eq("test-correlation-id"), contains("LOANED"));
    }
}

