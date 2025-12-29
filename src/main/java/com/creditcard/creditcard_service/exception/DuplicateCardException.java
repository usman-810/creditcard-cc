package com.creditcard.creditcard_service.exception;



public class DuplicateCardException extends RuntimeException {
    public DuplicateCardException(String message) {
        super(message);
    }
}