package com.library.borrowing.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Member entity representing a library member with their account details.
 */
@Entity
@Table(name = "member", 
       indexes = {
           @Index(name = "idx_member_email", columnList = "email")
       })
public class Member {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;
    
    @Column(name = "member_status", nullable = false, length = 20)
    private String memberStatus; // ACTIVE, INACTIVE, SUSPENDED
    
    @Column(name = "outstanding_fine_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal outstandingFineBalance; // In currency units
    
    @Column(name = "active_loan_count", nullable = false)
    private Integer activeLoanCount; // Cache for performance
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;
    
    @Version
    private Long version; // For optimistic locking

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
        if (memberStatus == null) {
            memberStatus = "ACTIVE";
        }
        if (outstandingFineBalance == null) {
            outstandingFineBalance = BigDecimal.ZERO;
        }
        if (activeLoanCount == null) {
            activeLoanCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }

    public Member() {
    }

    public Member(String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
        this.memberStatus = "ACTIVE";
        this.outstandingFineBalance = BigDecimal.ZERO;
        this.activeLoanCount = 0;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }

    public BigDecimal getOutstandingFineBalance() {
        return outstandingFineBalance;
    }

    public void setOutstandingFineBalance(BigDecimal outstandingFineBalance) {
        this.outstandingFineBalance = outstandingFineBalance;
    }

    public Integer getActiveLoanCount() {
        return activeLoanCount;
    }

    public void setActiveLoanCount(Integer activeLoanCount) {
        this.activeLoanCount = activeLoanCount;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", memberStatus='" + memberStatus + '\'' +
                ", outstandingFineBalance=" + outstandingFineBalance +
                ", activeLoanCount=" + activeLoanCount +
                '}';
    }
}

