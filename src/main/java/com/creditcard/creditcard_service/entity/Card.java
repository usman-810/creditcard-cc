package com.creditcard.creditcard_service.entity;




import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.creditcard.creditcard_service.enums.CardStatus;
import com.creditcard.creditcard_service.enums.CardType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards", indexes = {
    @Index(name = "idx_card_number", columnList = "card_number"),
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", nullable = false, unique = true, length = 16)
    private String cardNumber;  // 16-digit card number

    @Column(name = "customer_id", nullable = false)
    private Long customerId;  // Reference to Customer Service

    @Column(name = "card_holder_name", nullable = false, length = 100)
    private String cardHolderName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;  // SILVER, GOLD, PLATINUM

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;  // ACTIVE, INACTIVE, BLOCKED, EXPIRED, CLOSED

    @Column(name = "credit_limit", nullable = false)
    private Double creditLimit;  // Total credit limit

    @Column(name = "available_credit", nullable = false)
    private Double availableCredit;  // Available credit balance

    @Column(name = "daily_limit", nullable = false)
    private Double dailyLimit;  // Daily transaction limit

    @Column(name = "cvv", nullable = false, length = 3)
    private String cvv;  // 3-digit CVV

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;  // Typically 5 years from issue date

    @Column(name = "activation_date")
    private LocalDateTime activationDate;

    @Column(name = "last_used_date")
    private LocalDateTime lastUsedDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isActive() {
        return status == CardStatus.ACTIVE && !isExpired();
    }

    public boolean canTransact(Double amount) {
        return isActive() && availableCredit >= amount;
    }
}