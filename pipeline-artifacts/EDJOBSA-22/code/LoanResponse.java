package com.library.borrowing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO for successful checkout.
 */
public class LoanResponse {
    
    @JsonProperty("loan_id")
    private UUID loanId;
    
    @JsonProperty("isbn")
    private String isbn;
    
    @JsonProperty("member_id")
    private UUID memberId;
    
    @JsonProperty("due_date")
    private LocalDate dueDate;
    
    @JsonProperty("loan_date")
    private LocalDate loanDate;

    public LoanResponse() {
    }

    public LoanResponse(UUID loanId, String isbn, UUID memberId, LocalDate dueDate, LocalDate loanDate) {
        this.loanId = loanId;
        this.isbn = isbn;
        this.memberId = memberId;
        this.dueDate = dueDate;
        this.loanDate = loanDate;
    }

    public UUID getLoanId() {
        return loanId;
    }

    public void setLoanId(UUID loanId) {
        this.loanId = loanId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    @Override
    public String toString() {
        return "LoanResponse{" +
                "loanId=" + loanId +
                ", isbn='" + isbn + '\'' +
                ", memberId=" + memberId +
                ", dueDate=" + dueDate +
                ", loanDate=" + loanDate +
                '}';
    }
}

