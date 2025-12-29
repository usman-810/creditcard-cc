package com.creditcard.creditcard_service.enums;

public enum CardStatus {
    ACTIVE,           // Card is active and can be used
    INACTIVE,         // Card issued but not activated yet
    BLOCKED,          // Card blocked by customer or system
    EXPIRED,          // Card expired
    CLOSED            // Card permanently closed
}