package com.creditcard.creditcard_service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.creditcard.creditcard_service.dto.CardRequestDTO;
import com.creditcard.creditcard_service.dto.CardResponseDTO;
import com.creditcard.creditcard_service.enums.CardStatus;
import com.creditcard.creditcard_service.enums.CardType;

import java.util.List;

public interface CardService {
	
	
//..	
    CardResponseDTO issueCard(CardRequestDTO requestDTO);

    CardResponseDTO getCardById(Long id);

    CardResponseDTO getCardByCardNumber(String cardNumber);

    List<CardResponseDTO> getCardsByCustomerId(Long customerId);

    Page<CardResponseDTO> getAllCards(Pageable pageable);

    CardResponseDTO activateCard(Long cardId);

    CardResponseDTO blockCard(Long cardId, String reason);

    CardResponseDTO unblockCard(Long cardId);

    CardResponseDTO updateCardStatus(Long cardId, CardStatus status);

    CardResponseDTO updateCreditLimit(Long cardId, Double newLimit);

    CardResponseDTO updateDailyLimit(Long cardId, Double newLimit);

    void closeCard(Long cardId);

    List<CardResponseDTO> getCardsByStatus(CardStatus status);

    List<CardResponseDTO> getCardsByType(CardType cardType);

    long getCardCountByCustomerId(Long customerId);

    Double getTotalCreditLimitByCustomerId(Long customerId);

    List<CardResponseDTO> getExpiringCards(int daysBeforeExpiry);

    boolean cardExists(String cardNumber);
    CardResponseDTO updateCardBalance(Long cardId, Double amount, Boolean isDebit);

}