package com.creditcard.creditcard_service.exception;



public class InvalidCardException extends RuntimeException {
    public InvalidCardException(String message) {
        super(message);
    }
}