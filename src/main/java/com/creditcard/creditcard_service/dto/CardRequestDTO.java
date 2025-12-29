package com.creditcard.creditcard_service.dto;




import com.creditcard.creditcard_service.enums.CardType;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardRequestDTO {

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be positive")
    private Long customerId;

    @NotBlank(message = "Card holder name is required")
    @Size(min = 3, max = 100, message = "Card holder name must be between 3 and 100 characters")
    private String cardHolderName;

    @NotNull(message = "Card type is required")
    private CardType cardType;  // SILVER, GOLD, PLATINUM

    @Positive(message = "Credit limit must be positive")
    private Double creditLimit;  // Optional: Override default limit

    @Positive(message = "Daily limit must be positive")
    private Double dailyLimit;  // Optional: Override default limit
}
