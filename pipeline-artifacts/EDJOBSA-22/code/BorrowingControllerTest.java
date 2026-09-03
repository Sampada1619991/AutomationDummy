package com.library.borrowing.controller;

import com.library.borrowing.dto.CheckoutRequest;
import com.library.borrowing.dto.LoanResponse;
import com.library.borrowing.exception.BookUnavailableException;
import com.library.borrowing.service.BorrowingService;
import com.library.borrowing.service.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for BorrowingController.
 * Tests the HTTP layer and integration with the service layer.
 */
@WebMvcTest(BorrowingController.class)
@DisplayName("BorrowingController Integration Tests")
class BorrowingControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private BorrowingService borrowingService;
    
    @MockBean
    private RateLimitService rateLimitService;
    
    private UUID memberId;
    private UUID loanId;
    private String isbn;
    
    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        loanId = UUID.randomUUID();
        isbn = "978-0-13-110362-7";
    }
    
    @Test
    @WithMockUser(username = "12345678-1234-1234-1234-123456789012", authorities = {"library:borrow"})
    @DisplayName("POST /api/v1/loans returns 201 Created on successful checkout")
    void testCheckoutSuccess() throws Exception {
        // Arrange
        CheckoutRequest request = new CheckoutRequest(isbn);
        LoanResponse mockResponse = new LoanResponse(
            loanId,
            isbn,
            memberId,
            LocalDate.now().plusDays(14),
            LocalDate.now()
        );
        
        when(rateLimitService.isAllowed(any(UUID.class))).thenReturn(true);
        when(borrowingService.checkoutBook(any(UUID.class), any(String.class), any(String.class)))
            .thenReturn(mockResponse);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/loans")
                .contentType("application/json")
                .header("X-Correlation-Id", "test-correlation-id")
                .content(objectMapper.writeValueAsString(request))
                .with(request1 -> {
                    request1.setAttribute("spring.security.core.Authentication",
                        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            memberId.toString(), null, java.util.List.of(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("library:borrow")
                            )
                        ));
                    return request1;
                }))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.loan_id").value(loanId.toString()))
            .andExpect(jsonPath("$.isbn").value(isbn))
            .andExpect(header().exists("X-Correlation-Id"));
    }
    
    @Test
    @WithMockUser(username = "12345678-1234-1234-1234-123456789012")
    @DisplayName("POST /api/v1/loans returns 429 when rate limit exceeded")
    void testCheckoutRateLimitExceeded() throws Exception {
        // Arrange
        CheckoutRequest request = new CheckoutRequest(isbn);
        when(rateLimitService.isAllowed(any(UUID.class))).thenReturn(false);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/loans")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isTooManyRequests());
    }
    
    @Test
    @WithMockUser(username = "12345678-1234-1234-1234-123456789012", authorities = {"library:borrow"})
    @DisplayName("POST /api/v1/loans returns 409 on book unavailable")
    void testCheckoutBookUnavailable() throws Exception {
        // Arrange
        CheckoutRequest request = new CheckoutRequest(isbn);
        when(rateLimitService.isAllowed(any(UUID.class))).thenReturn(true);
        when(borrowingService.checkoutBook(any(UUID.class), any(String.class), any(String.class)))
            .thenThrow(new BookUnavailableException("Book is currently loaned out"));
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/loans")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(request1 -> {
                    request1.setAttribute("spring.security.core.Authentication",
                        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            memberId.toString(), null, java.util.List.of(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("library:borrow")
                            )
                        ));
                    return request1;
                }))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("BOOK_UNAVAILABLE"));
    }
    
    @Test
    @DisplayName("POST /api/v1/loans returns 400 with invalid ISBN")
    void testCheckoutInvalidIsbn() throws Exception {
        // Arrange
        CheckoutRequest request = new CheckoutRequest("invalid-isbn");
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/loans")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }
}

