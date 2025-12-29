package com.creditcard.creditcard_service.enums;



public enum CardType {
    SILVER(50000.0, 10000.0),      // Credit limit: 50k, Daily limit: 10k
    GOLD(100000.0, 25000.0),       // Credit limit: 100k, Daily limit: 25k
    PLATINUM(500000.0, 100000.0);  // Credit limit: 500k, Daily limit: 100k

    private final Double defaultCreditLimit;
    private final Double defaultDailyLimit;

    CardType(Double defaultCreditLimit, Double defaultDailyLimit) {
        this.defaultCreditLimit = defaultCreditLimit;
        this.defaultDailyLimit = defaultDailyLimit;
    }

    public Double getDefaultCreditLimit() {
        return defaultCreditLimit;
    }

    public Double getDefaultDailyLimit() {
        return defaultDailyLimit;
    }
}
