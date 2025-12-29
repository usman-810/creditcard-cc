package com.creditcard.creditcard_service.mapper;



import org.springframework.stereotype.Component;

import com.creditcard.creditcard_service.dto.CardRequestDTO;
import com.creditcard.creditcard_service.dto.CardResponseDTO;
import com.creditcard.creditcard_service.entity.Card;
import com.creditcard.creditcard_service.util.CardNumberGenerator;

@Component
public class CardMapper {

    public Card toEntity(CardRequestDTO dto) {
        Card card = new Card();
        card.setCustomerId(dto.getCustomerId());
        card.setCardHolderName(dto.getCardHolderName());
        card.setCardType(dto.getCardType());
        
        // Use provided limits or defaults from CardType
        if (dto.getCreditLimit() != null) {
            card.setCreditLimit(dto.getCreditLimit());
        } else {
            card.setCreditLimit(dto.getCardType().getDefaultCreditLimit());
        }
        
        if (dto.getDailyLimit() != null) {
            card.setDailyLimit(dto.getDailyLimit());
        } else {
            card.setDailyLimit(dto.getCardType().getDefaultDailyLimit());
        }
        
        card.setAvailableCredit(card.getCreditLimit());
        
        return card;
    }

    public CardResponseDTO toDTO(Card card) {
        CardResponseDTO dto = new CardResponseDTO();
        dto.setId(card.getId());
        dto.setCardNumber(card.getCardNumber());
        dto.setMaskedCardNumber(CardNumberGenerator.maskCardNumber(card.getCardNumber()));
        dto.setCustomerId(card.getCustomerId());
        dto.setCardHolderName(card.getCardHolderName());
        dto.setCardType(card.getCardType());
        dto.setStatus(card.getStatus());
        dto.setCreditLimit(card.getCreditLimit());
        dto.setAvailableCredit(card.getAvailableCredit());
        dto.setDailyLimit(card.getDailyLimit());
        dto.setIssueDate(card.getIssueDate());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setActivationDate(card.getActivationDate());
        dto.setLastUsedDate(card.getLastUsedDate());
        dto.setCreatedAt(card.getCreatedAt());
        dto.setUpdatedAt(card.getUpdatedAt());
        
        // Note: CVV is never returned for security
        return dto;
    }
}