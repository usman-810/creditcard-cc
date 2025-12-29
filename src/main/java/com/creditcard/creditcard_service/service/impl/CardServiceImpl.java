package com.creditcard.creditcard_service.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.creditcard.creditcard_service.CardService;
import com.creditcard.creditcard_service.dto.ApiResponse;
import com.creditcard.creditcard_service.dto.CardRequestDTO;
import com.creditcard.creditcard_service.dto.CardResponseDTO;
import com.creditcard.creditcard_service.entity.Card;
import com.creditcard.creditcard_service.enums.CardStatus;
import com.creditcard.creditcard_service.enums.CardType;
import com.creditcard.creditcard_service.exception.CardNotFoundException;
import com.creditcard.creditcard_service.exception.CustomerNotFoundException;
import com.creditcard.creditcard_service.exception.InvalidCardException;
import com.creditcard.creditcard_service.feign.CustomerServiceClient;
import com.creditcard.creditcard_service.mapper.CardMapper;
import com.creditcard.creditcard_service.repository.CardRepository;
import com.creditcard.creditcard_service.util.CardNumberGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final CustomerServiceClient customerServiceClient;

    @Override
    public CardResponseDTO issueCard(CardRequestDTO requestDTO) {
        log.info("Issuing card for customer ID: {}", requestDTO.getCustomerId());

        // Verify customer exists using Feign client
        ApiResponse<Boolean> customerExistsResponse = 
            customerServiceClient.customerExists(requestDTO.getCustomerId());
        
        if (!customerExistsResponse.isSuccess() || !customerExistsResponse.getData()) {
            log.error("Customer not found with ID: {}", requestDTO.getCustomerId());
            throw new CustomerNotFoundException(
                "Customer not found with ID: " + requestDTO.getCustomerId());
        }

        // Generate unique card number
        String cardNumber;
        do {
            cardNumber = CardNumberGenerator.generateCardNumber();
        } while (cardRepository.existsByCardNumber(cardNumber));

        // Create card entity
        Card card = cardMapper.toEntity(requestDTO);
        card.setCardNumber(cardNumber);
        card.setCvv(CardNumberGenerator.generateCVV());
        card.setStatus(CardStatus.INACTIVE);  // Card needs to be activated
        card.setIssueDate(LocalDate.now());
        card.setExpiryDate(LocalDate.now().plusYears(5));  // 5 years validity

        Card savedCard = cardRepository.save(card);
        log.info("Card issued successfully with ID: {}", savedCard.getId());

        return cardMapper.toDTO(savedCard);
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponseDTO getCardById(Long id) {
        log.info("Fetching card with ID: {}", id);
        
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + id));

        return cardMapper.toDTO(card);
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponseDTO getCardByCardNumber(String cardNumber) {
        log.info("Fetching card with card number: {}", 
                 CardNumberGenerator.maskCardNumber(cardNumber));
        
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException(
                    "Card not found with number: " + CardNumberGenerator.maskCardNumber(cardNumber)));

        return cardMapper.toDTO(card);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardResponseDTO> getCardsByCustomerId(Long customerId) {
        log.info("Fetching all cards for customer ID: {}", customerId);
        
        List<Card> cards = cardRepository.findByCustomerId(customerId);
        return cards.stream()
                .map(cardMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardResponseDTO> getAllCards(Pageable pageable) {
        log.info("Fetching all cards with pagination");
        
        return cardRepository.findAll(pageable)
                .map(cardMapper::toDTO);
    }

    @Override
    public CardResponseDTO activateCard(Long cardId) {
        log.info("Activating card with ID: {}", cardId);
        
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new InvalidCardException("Card is already active");
        }

        if (card.getStatus() == CardStatus.BLOCKED || card.getStatus() == CardStatus.CLOSED) {
            throw new InvalidCardException("Cannot activate a blocked or closed card");
        }

        if (card.isExpired()) {
            throw new InvalidCardException("Cannot activate an expired card");
        }

        card.setStatus(CardStatus.ACTIVE);
        card.setActivationDate(LocalDateTime.now());

        Card savedCard = cardRepository.save(card);
        log.info("Card activated successfully");

        return cardMapper.toDTO(savedCard);
    }

    @Override
    public CardResponseDTO blockCard(Long cardId, String reason) {
        log.info("Blocking card with ID: {} - Reason: {}", cardId, reason);
        
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        if (card.getStatus() == CardStatus.CLOSED) {
            throw new InvalidCardException("Cannot block a closed card");
        }

        card.setStatus(CardStatus.BLOCKED);
        Card savedCard = cardRepository.save(card);
        
        log.info("Card blocked successfully");
        return cardMapper.toDTO(savedCard);
    }

    @Override
    public CardResponseDTO unblockCard(Long cardId) {
        log.info("Unblocking card with ID: {}", cardId);
        
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        if (card.getStatus() != CardStatus.BLOCKED) {
            throw new InvalidCardException("Only blocked cards can be unblocked");
        }

        if (card.isExpired()) {
            throw new InvalidCardException("Cannot unblock an expired card");
        }

        card.setStatus(CardStatus.ACTIVE);
        Card savedCard = cardRepository.save(card);
        
        log.info("Card unblocked successfully");
        return cardMapper.toDTO(savedCard);
    }

    @Override
    public CardResponseDTO updateCardStatus(Long cardId, CardStatus status) {
        log.info("Updating card status to: {} for card ID: {}", status, cardId);
        
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        card.setStatus(status);
        Card savedCard = cardRepository.save(card);
        
        log.info("Card status updated successfully");
        return cardMapper.toDTO(savedCard);
    }

    @Override
    public CardResponseDTO updateCreditLimit(Long cardId, Double newLimit) {
        log.info("Updating credit limit to {} for card ID: {}", newLimit, cardId);
        
        if (newLimit <= 0) {
            throw new InvalidCardException("Credit limit must be positive");
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        // Adjust available credit proportionally
        double utilizationRatio = 1 - (card.getAvailableCredit() / card.getCreditLimit());
        card.setCreditLimit(newLimit);
        card.setAvailableCredit(newLimit * (1 - utilizationRatio));

        Card savedCard = cardRepository.save(card);
        log.info("Credit limit updated successfully");

        return cardMapper.toDTO(savedCard);
    }

    @Override
    public CardResponseDTO updateDailyLimit(Long cardId, Double newLimit) {
        log.info("Updating daily limit to {} for card ID: {}", newLimit, cardId);
        
        if (newLimit <= 0) {
            throw new InvalidCardException("Daily limit must be positive");
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        card.setDailyLimit(newLimit);
        Card savedCard = cardRepository.save(card);
        
        log.info("Daily limit updated successfully");
        return cardMapper.toDTO(savedCard);
    }

    @Override
    public void closeCard(Long cardId) {
        log.info("Closing card with ID: {}", cardId);
        
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        if (card.getStatus() == CardStatus.CLOSED) {
            throw new InvalidCardException("Card is already closed");
        }

        card.setStatus(CardStatus.CLOSED);
        cardRepository.save(card);
        
        log.info("Card closed successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardResponseDTO> getCardsByStatus(CardStatus status) {
        log.info("Fetching cards with status: {}", status);
        
        return cardRepository.findByStatus(status).stream()
                .map(cardMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardResponseDTO> getCardsByType(CardType cardType) {
        log.info("Fetching cards of type: {}", cardType);
        
        return cardRepository.findByCardType(cardType).stream()
                .map(cardMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getCardCountByCustomerId(Long customerId) {
        return cardRepository.countByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalCreditLimitByCustomerId(Long customerId) {
        Double total = cardRepository.getTotalCreditLimitByCustomerId(customerId);
        return total != null ? total : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardResponseDTO> getExpiringCards(int daysBeforeExpiry) {
        LocalDate expiryDate = LocalDate.now().plusDays(daysBeforeExpiry);
        
        return cardRepository.findExpiringCards(expiryDate).stream()
                .map(cardMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean cardExists(String cardNumber) {
        return cardRepository.existsByCardNumber(cardNumber);
    }
    
    
    
  
    
    
    @Override
    public CardResponseDTO updateCardBalance(Long cardId, Double amount, Boolean isDebit) {
        log.info("Updating balance for card: {} by {} (debit: {})", cardId, amount, isDebit);
        
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        // Check if card is active
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardException("Card is not active. Cannot process transaction. Status: " + card.getStatus());
        }

        if (isDebit) {
            // Debit: Subtract from available credit (purchase)
            if (card.getAvailableCredit() < amount) {
                throw new InvalidCardException(
                    String.format("Insufficient credit balance. Available: %.2f, Required: %.2f", 
                        card.getAvailableCredit(), amount)
                );
            }
            card.setAvailableCredit(card.getAvailableCredit() - amount);
            log.info("Debited {} from card {}. New balance: {}", amount, cardId, card.getAvailableCredit());
        } else {
            // Credit: Add to available credit (payment/refund)
            Double newBalance = card.getAvailableCredit() + amount;
            
            // Don't exceed credit limit
            if (newBalance > card.getCreditLimit()) {
                newBalance = card.getCreditLimit();
            }
            card.setAvailableCredit(newBalance);
            log.info("Credited {} to card {}. New balance: {}", amount, cardId, card.getAvailableCredit());
        }

        Card savedCard = cardRepository.save(card);
        log.info("Card balance updated successfully. Card ID: {}, New balance: {}", 
            savedCard.getId(), savedCard.getAvailableCredit());

        return cardMapper.toDTO(savedCard);
    }
}