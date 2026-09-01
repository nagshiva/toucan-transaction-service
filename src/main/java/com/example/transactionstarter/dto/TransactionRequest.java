package com.example.transactionstarter.dto;

import java.math.BigDecimal;

import com.example.transactionstarter.model.TransactionType;
import com.example.transactionstarter.validation.ValidCurrency;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class TransactionRequest {

    @NotBlank(message = "Customer ID is required")
    @Size(max = 50, message = "Customer ID must not exceed 50 characters")
    @Pattern(
            regexp = "^$|^[A-Za-z0-9_-]+$",
            message = "Customer ID contains invalid characters"
    )
    private String customerId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Digits(
            integer = 17,
            fraction = 2,
            message = "Amount must have at most 2 decimal places"
    )
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @ValidCurrency
    private String currency;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    public TransactionRequest() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}