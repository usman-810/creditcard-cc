package com.creditcard.creditcard_service.repository;




import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.creditcard.creditcard_service.entity.Card;
import com.creditcard.creditcard_service.enums.CardStatus;
import com.creditcard.creditcard_service.enums.CardType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    List<Card> findByCustomerId(Long customerId);

    Page<Card> findByCustomerId(Long customerId, Pageable pageable);

    boolean existsByCardNumber(String cardNumber);

    List<Card> findByStatus(CardStatus status);

    Page<Card> findByStatus(CardStatus status, Pageable pageable);

    List<Card> findByCardType(CardType cardType);

    long countByCustomerId(Long customerId);

    long countByStatus(CardStatus status);

    @Query("SELECT c FROM Card c WHERE c.expiryDate <= :expiryDate AND c.status = 'ACTIVE'")
    List<Card> findExpiringCards(@Param("expiryDate") LocalDate expiryDate);

    @Query("SELECT c FROM Card c WHERE c.customerId = :customerId AND c.status = 'ACTIVE'")
    List<Card> findActiveCardsByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT c FROM Card c WHERE c.cardNumber LIKE %:lastFourDigits")
    List<Card> findByLastFourDigits(@Param("lastFourDigits") String lastFourDigits);

    @Query("SELECT SUM(c.creditLimit) FROM Card c WHERE c.customerId = :customerId AND c.status = 'ACTIVE'")
    Double getTotalCreditLimitByCustomerId(@Param("customerId") Long customerId);
}