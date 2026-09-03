package com.library.borrowing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for book checkout.
 * Accepts ISBN-13 in standard or hyphenated format.
 */
public class CheckoutRequest {
    
    @NotBlank(message = "ISBN is required")
    @Pattern(
        regexp = "(?:ISBN(?:-1[03])?[:\\.\\s]?)?(?=[0-9X]{10}$|(?:(?=(?:[0-9]+[:\\.\\s]){3})[0-9X][:\\.\\s]?[0-9][:\\.\\s]?[0-9][:\\.\\s]?[0-9X]$|97[89][:\\.\\s]?[0-9]{1,5}[:\\.\\s]?[0-9]+[:\\.\\s]?[0-9]+[:\\.\\s]?[0-9X]$))(?:97[89])?[:\\.\\s]?[0-9]{1,5}[:\\.\\s]?[0-9]+[:\\.\\s]?[0-9X]",
        message = "ISBN must be a valid ISBN-13 (with or without hyphens)"
    )
    @JsonProperty("isbn")
    private String isbn;

    public CheckoutRequest() {
    }

    public CheckoutRequest(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        return "CheckoutRequest{" +
                "isbn='" + isbn + '\'' +
                '}';
    }
}

