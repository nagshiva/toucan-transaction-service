package com.example.transactionstarter.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.transactionstarter.dto.TransactionRequest;
import com.example.transactionstarter.dto.TransactionStatusUpdateRequest;
import com.example.transactionstarter.model.Transaction;
import com.example.transactionstarter.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // A. Create transaction

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction createTransaction(
            @Valid @RequestBody TransactionRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        return transactionService.createTransaction(
                request,
                idempotencyKey);
    }


    // B. Get transaction by Transaction ID

    @GetMapping("/{transactionId}")
    public Transaction getTransaction(
            @PathVariable String transactionId) {

        return transactionService.getTransaction(transactionId);
    }


    // C. Update transaction status

    @PatchMapping("/{transactionId}/status")
    public Transaction updateTransactionStatus(
            @PathVariable String transactionId,
            @Valid @RequestBody TransactionStatusUpdateRequest request) {

        return transactionService.updateTransactionStatus(
                transactionId,
                request.getStatus());
    }


    // D. Get all transactions for a Customer ID

    @GetMapping("/customer/{customerId}/transactions")
    public List<Transaction> getCustomerTransactions(
            @PathVariable String customerId) {

        return transactionService.getCustomerTransactions(customerId);
    }
}