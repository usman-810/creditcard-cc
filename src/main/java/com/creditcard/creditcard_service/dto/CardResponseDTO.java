package com.creditcard.creditcard_service.dto;


import java.time.LocalDate;
import java.time.LocalDateTime;

import com.creditcard.creditcard_service.enums.CardStatus;
import com.creditcard.creditcard_service.enums.CardType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardResponseDTO {

    private Long id;
    private String cardNumber;
    private String maskedCardNumber;  // e.g., "XXXX XXXX XXXX 1234"
    private Long customerId;
    private String cardHolderName;
    private CardType cardType;
    private CardStatus status;
    private Double creditLimit;
    private Double availableCredit;
    private Double dailyLimit;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issueDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime activationDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUsedDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // CVV is never returned in response for security
}