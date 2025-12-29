package com.creditcard.creditcard_service.util;


import java.security.SecureRandom;
import java.util.Random;

public class CardNumberGenerator {

    private static final Random random = new SecureRandom();

    /**
     * Generates a 16-digit card number using Luhn algorithm
     * Format: XXXX XXXX XXXX XXXX
     */
    public static String generateCardNumber() {
        // First 6 digits: BIN (Bank Identification Number)
        String bin = "456789";  // Example BIN
        
        // Next 9 digits: Random account number
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            accountNumber.append(random.nextInt(10));
        }
        
        // Combine BIN + Account Number
        String cardNumberWithoutChecksum = bin + accountNumber.toString();
        
        // Calculate checksum digit using Luhn algorithm
        int checksum = calculateLuhnChecksum(cardNumberWithoutChecksum);
        
        return cardNumberWithoutChecksum + checksum;
    }

    /**
     * Generates a 3-digit CVV
     */
    public static String generateCVV() {
        return String.format("%03d", random.nextInt(1000));
    }

    /**
     * Luhn algorithm to calculate checksum digit
     */
    private static int calculateLuhnChecksum(String cardNumber) {
        int sum = 0;
        boolean alternate = true;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Integer.parseInt(cardNumber.substring(i, i + 1));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return (10 - (sum % 10)) % 10;
    }

    /**
     * Validates card number using Luhn algorithm
     */
    public static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return false;
        }
        
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Integer.parseInt(cardNumber.substring(i, i + 1));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return (sum % 10 == 0);
    }

    /**
     * Masks card number for display (e.g., XXXX XXXX XXXX 1234)
     */
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return cardNumber;
        }
        
        String lastFour = cardNumber.substring(12);
        return "XXXX XXXX XXXX " + lastFour;
    }

    /**
     * Formats card number with spaces (e.g., 4567 8901 2345 6789)
     */
    public static String formatCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return cardNumber;
        }
        
        return cardNumber.substring(0, 4) + " " +
               cardNumber.substring(4, 8) + " " +
               cardNumber.substring(8, 12) + " " +
               cardNumber.substring(12);
    }
}