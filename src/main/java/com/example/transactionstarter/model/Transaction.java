package com.example.transactionstarter.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @NotBlank(message = "Transaction ID is required")
    @Size(max = 50, message = "Transaction ID must not exceed 50 characters")
    @Column(name = "transaction_id", nullable = false, updatable = false)
    private String transactionId;

    @NotBlank(message = "Customer ID is required")
    @Size(max = 50, message = "Customer ID must not exceed 50 characters")
    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must contain exactly 3 characters")
    @Column(nullable = false, length = 3)
    private String currency;

    @NotBlank(message = "Transaction type is required")
    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @NotBlank(message = "Transaction status is required")
    @Column(name = "transaction_status", nullable = false)
    private String transactionStatus;

    public Transaction() {
    }

    public Transaction(String transactionId, String customerId,
                       BigDecimal amount, String currency,
                       String transactionType, String transactionStatus) {
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}