package com.library.borrowing.controller;

import com.library.borrowing.dto.CheckoutRequest;
import com.library.borrowing.dto.LoanResponse;
import com.library.borrowing.exception.BorrowingException;
import com.library.borrowing.service.BorrowingService;
import com.library.borrowing.service.RateLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

/**
 * REST Controller for self-service book borrowing.
 * 
 * Endpoint: POST /api/v1/loans
 * 
 * This controller:
 * 1. Extracts memberId from the JWT token (OAuth2 Resource Server)
 * 2. Validates the request payload (ISBN-13)
 * 3. Checks rate limiting
 * 4. Delegates to BorrowingService for business logic
 * 5. Maps domain exceptions to HTTP status codes
 * 
 * All endpoints require OAuth2 scope "library:borrow".
 * All requests should include X-Correlation-Id header for tracing.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@Validated
public class BorrowingController {
    
    private final BorrowingService borrowingService;
    private final RateLimitService rateLimitService;

    @Autowired
    public BorrowingController(BorrowingService borrowingService, RateLimitService rateLimitService) {
        this.borrowingService = borrowingService;
        this.rateLimitService = rateLimitService;
    }

    /**
     * Checkout a book for the authenticated member.
     * 
     * @param request the checkout request with ISBN
     * @param httpRequest the HTTP request (for correlation ID and client info)
     * @return LoanResponse with HTTP 201 Created on success
     * @throws BorrowingException on business logic errors (fines, limit, unavailable, etc)
     */
    @PostMapping("/loans")
    @PreAuthorize("hasAuthority('library:borrow')")
    public ResponseEntity<LoanResponse> checkoutBook(
            @Valid @RequestBody CheckoutRequest request,
            HttpServletRequest httpRequest) {
        
        // Extract memberId from JWT token (set by Spring Security)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID memberId = UUID.fromString(authentication.getName()); // JWT 'sub' claim
        
        // Extract or generate correlation ID for request tracing
        String correlationId = httpRequest.getHeader("X-Correlation-Id");
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        log.info("Checkout request: memberId={}, isbn={}, correlationId={}", 
            memberId, request.getIsbn(), correlationId);
        
        // Check rate limit
        if (!rateLimitService.isAllowed(memberId)) {
            log.warn("Rate limit exceeded for member: {}, correlationId={}", memberId, correlationId);
            return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Correlation-Id", correlationId)
                .build();
        }
        
        // Perform checkout
        LoanResponse loanResponse = borrowingService.checkoutBook(memberId, request.getIsbn(), correlationId);
        
        log.info("Checkout successful: loanId={}, correlationId={}", loanResponse.getLoanId(), correlationId);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("X-Correlation-Id", correlationId)
            .body(loanResponse);
    }
}

