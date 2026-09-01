package com.example.transactionstarter.dto;

import jakarta.validation.constraints.NotNull;

import com.example.transactionstarter.model.TransactionStatus;

public class TransactionStatusUpdateRequest {

    @NotNull(message = "Transaction status is required")
    private TransactionStatus status;

    public TransactionStatusUpdateRequest() {
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}