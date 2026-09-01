package com.example.transactionstarter.exception;

public class InvalidTransactionStatusException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidTransactionStatusException(String message) {
        super(message);
    }
}