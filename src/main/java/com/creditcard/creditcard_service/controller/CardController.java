package com.creditcard.creditcard_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.creditcard.creditcard_service.CardService;
import com.creditcard.creditcard_service.dto.ApiResponse;
import com.creditcard.creditcard_service.dto.BalanceUpdateRequest;
import com.creditcard.creditcard_service.dto.CardRequestDTO;
import com.creditcard.creditcard_service.dto.CardResponseDTO;
import com.creditcard.creditcard_service.enums.CardStatus;
import com.creditcard.creditcard_service.enums.CardType;

import java.util.List;
//demo
@Slf4j
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "APIs for managing credit cards")
//@CrossOrigin(origins = "*")
public class CardController {

    private final CardService cardService;

    @Operation(
        summary = "Issue a new card",
        description = "Issues a new credit card for a customer. Card number is auto-generated."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Card issued successfully",
            content = @Content(schema = @Schema(implementation = CardResponseDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input data"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Customer not found"
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CardResponseDTO>> issueCard(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Card issue request",
                required = true,
                content = @Content(schema = @Schema(implementation = CardRequestDTO.class))
            )
            @Valid @RequestBody CardRequestDTO requestDTO) {
        
        log.info("REST request to issue card for customer: {}", requestDTO.getCustomerId());
        
        CardResponseDTO response = cardService.issueCard(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Card issued successfully"));
    }

    @Operation(
        summary = "Get card by ID",
        description = "Retrieves card details by card ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CardResponseDTO>> getCardById(
            @Parameter(description = "Card ID", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("REST request to get card: {}", id);
        
        CardResponseDTO response = cardService.getCardById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Card retrieved successfully"));
    }

    @Operation(
        summary = "Get card by card number",
        description = "Retrieves card details by 16-digit card number"
    )
    @GetMapping("/number/{cardNumber}")
    public ResponseEntity<ApiResponse<CardResponseDTO>> getCardByCardNumber(
            @Parameter(description = "16-digit card number", required = true, example = "4567890123456789")
            @PathVariable String cardNumber) {
        
        log.info("REST request to get card by number");
        
        CardResponseDTO response = cardService.getCardByCardNumber(cardNumber);
        return ResponseEntity.ok(ApiResponse.success(response, "Card retrieved successfully"));
    }

    @Operation(
        summary = "Get all cards for a customer",
        description = "Retrieves all cards associated with a customer ID"
    )
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<CardResponseDTO>>> getCardsByCustomerId(
            @Parameter(description = "Customer ID", required = true, example = "1")
            @PathVariable Long customerId) {
        
        log.info("REST request to get cards for customer: {}", customerId);
        
        List<CardResponseDTO> response = cardService.getCardsByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success(response, 
            "Cards retrieved successfully. Total cards: " + response.size()));
    }

    @Operation(
        summary = "Get all cards",
        description = "Retrieves all cards with pagination and sorting"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CardResponseDTO>>> getAllCards(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "DESC")
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        log.info("REST request to get all cards - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? 
                    Sort.by(sortBy).descending() : 
                    Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CardResponseDTO> response = cardService.getAllCards(pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Cards retrieved successfully"));
    }

    @Operation(
        summary = "Activate card",
        description = "Activates an inactive card. Card must not be blocked, closed, or expired."
    )
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<CardResponseDTO>> activateCard(
            @Parameter(description = "Card ID", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("REST request to activate card: {}", id);
        
        CardResponseDTO response = cardService.activateCard(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Card activated successfully"));
    }

    @Operation(
        summary = "Block card",
        description = "Blocks a card to prevent transactions. Card can be unblocked later."
    )
    @PatchMapping("/{id}/block")
    public ResponseEntity<ApiResponse<CardResponseDTO>> blockCard(
            @Parameter(description = "Card ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Reason for blocking", example = "Lost card")
            @RequestParam(required = false) String reason) {
        
        log.info("REST request to block card: {}", id);
        
        CardResponseDTO response = cardService.blockCard(id, reason);
        return ResponseEntity.ok(ApiResponse.success(response, "Card blocked successfully"));
    }

    @Operation(
        summary = "Unblock card",
        description = "Unblocks a previously blocked card"
    )
    @PatchMapping("/{id}/unblock")
    public ResponseEntity<ApiResponse<CardResponseDTO>> unblockCard(
            @Parameter(description = "Card ID", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("REST request to unblock card: {}", id);
        
        CardResponseDTO response = cardService.unblockCard(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Card unblocked successfully"));
    }

    @Operation(
        summary = "Update card status",
        description = "Updates the status of a card"
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CardResponseDTO>> updateCardStatus(
            @Parameter(description = "Card ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "New card status", required = true)
            @RequestParam CardStatus status) {
        
        log.info("REST request to update card status: {} to {}", id, status);
        
        CardResponseDTO response = cardService.updateCardStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Card status updated successfully"));
    }

    @Operation(
        summary = "Update credit limit",
        description = "Updates the credit limit of a card"
    )
    @PatchMapping("/{id}/credit-limit")
    public ResponseEntity<ApiResponse<CardResponseDTO>> updateCreditLimit(
            @Parameter(description = "Card ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "New credit limit", required = true, example = "100000.0")
            @RequestParam Double limit) {
        
        log.info("REST request to update credit limit for card: {} to {}", id, limit);
        
        CardResponseDTO response = cardService.updateCreditLimit(id, limit);
        return ResponseEntity.ok(ApiResponse.success(response, "Credit limit updated successfully"));
    }

    @Operation(
        summary = "Update daily limit",
        description = "Updates the daily transaction limit of a card"
    )
    @PatchMapping("/{id}/daily-limit")
    public ResponseEntity<ApiResponse<CardResponseDTO>> updateDailyLimit(
            @Parameter(description = "Card ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "New daily limit", required = true, example = "25000.0")
            @RequestParam Double limit) {
        
        log.info("REST request to update daily limit for card: {} to {}", id, limit);
        
        CardResponseDTO response = cardService.updateDailyLimit(id, limit);
        return ResponseEntity.ok(ApiResponse.success(response, "Daily limit updated successfully"));
    }

    @Operation(
        summary = "Close card",
        description = "Permanently closes a card. This action cannot be undone."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> closeCard(
            @Parameter(description = "Card ID", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("REST request to close card: {}", id);
        
        cardService.closeCard(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Card closed successfully"));
    }

    @Operation(
        summary = "Get cards by status",
        description = "Retrieves all cards with a specific status"
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<CardResponseDTO>>> getCardsByStatus(
            @Parameter(description = "Card status", required = true)
            @PathVariable CardStatus status) {
        
        log.info("REST request to get cards by status: {}", status);
        
        List<CardResponseDTO> response = cardService.getCardsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(response, 
            "Cards retrieved successfully. Total: " + response.size()));
    }

    @Operation(
        summary = "Get cards by type",
        description = "Retrieves all cards of a specific type (SILVER, GOLD, PLATINUM)"
    )
    @GetMapping("/type/{cardType}")
    public ResponseEntity<ApiResponse<List<CardResponseDTO>>> getCardsByType(
            @Parameter(description = "Card type", required = true)
            @PathVariable CardType cardType) {
        
        log.info("REST request to get cards by type: {}", cardType);
        
        List<CardResponseDTO> response = cardService.getCardsByType(cardType);
        return ResponseEntity.ok(ApiResponse.success(response, 
            "Cards retrieved successfully. Total: " + response.size()));
    }

    @Operation(
        summary = "Count cards by customer",
        description = "Returns the number of cards owned by a customer"
    )
    @GetMapping("/customer/{customerId}/count")
    public ResponseEntity<ApiResponse<Long>> getCardCountByCustomerId(
            @Parameter(description = "Customer ID", required = true, example = "1")
            @PathVariable Long customerId) {
        
        log.info("REST request to count cards for customer: {}", customerId);
        
        long count = cardService.getCardCountByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success(count, 
            "Customer has " + count + " card(s)"));
    }

    @Operation(
        summary = "Get total credit limit by customer",
        description = "Returns the total credit limit across all active cards for a customer"
    )
    @GetMapping("/customer/{customerId}/total-credit")
    public ResponseEntity<ApiResponse<Double>> getTotalCreditLimitByCustomerId(
            @Parameter(description = "Customer ID", required = true, example = "1")
            @PathVariable Long customerId) {
        
        log.info("REST request to get total credit limit for customer: {}", customerId);
        
        Double total = cardService.getTotalCreditLimitByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success(total, 
            "Total credit limit: " + total));
    }

    @Operation(
        summary = "Get expiring cards",
        description = "Retrieves cards that will expire within specified number of days"
    )
    @GetMapping("/expiring")
    public ResponseEntity<ApiResponse<List<CardResponseDTO>>> getExpiringCards(
            @Parameter(description = "Days before expiry", example = "30")
            @RequestParam(defaultValue = "30") int days) {
        
        log.info("REST request to get cards expiring in {} days", days);
        
        List<CardResponseDTO> response = cardService.getExpiringCards(days);
        return ResponseEntity.ok(ApiResponse.success(response, 
            response.size() + " card(s) expiring in " + days + " days"));
    }

    @Operation(
        summary = "Check if card exists",
        description = "Checks if a card exists with the given card number"
    )
    @GetMapping("/exists/{cardNumber}")
    public ResponseEntity<ApiResponse<Boolean>> cardExists(
            @Parameter(description = "16-digit card number", required = true)
            @PathVariable String cardNumber) {
        
        log.info("REST request to check if card exists");
        
        boolean exists = cardService.cardExists(cardNumber);
        return ResponseEntity.ok(ApiResponse.success(exists, 
            exists ? "Card exists" : "Card does not exist"));
    }
    
    
    
    @Operation(
    	    summary = "Update card balance",
    	    description = "Updates the available credit balance of a card (for transaction processing)"
    	)
    	@PostMapping("/{id}/update-balance")
    	public ResponseEntity<ApiResponse<CardResponseDTO>> updateCardBalance(
    	        @Parameter(description = "Card ID", required = true, example = "1")
    	        @PathVariable Long id,
    	        @io.swagger.v3.oas.annotations.parameters.RequestBody(
    	            description = "Balance update request containing amount and debit flag",
    	            required = true
    	        )
    	        @RequestBody BalanceUpdateRequest request) {
    	    
    	    log.info("REST request to update balance for card: {}, amount: {}, isDebit: {}", 
    	        id, request.getAmount(), request.getIsDebit());
    	    
    	    CardResponseDTO response = cardService.updateCardBalance(
    	        id, 
    	        request.getAmount(), 
    	        request.getIsDebit()
    	    );
    	    
    	    return ResponseEntity.ok(ApiResponse.success(response, "Card balance updated successfully"));
    	}
    
}