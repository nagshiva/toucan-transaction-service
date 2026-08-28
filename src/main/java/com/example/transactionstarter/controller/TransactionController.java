package com.example.transactionstarter.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.transactionstarter.dto.TransactionRequest;
import com.example.transactionstarter.model.Transaction;
import com.example.transactionstarter.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction createTransaction(
            @Valid @RequestBody TransactionRequest request) {

        return transactionService.createTransaction(request);
    }
    @GetMapping("/{transactionId}")
    public Transaction getTransaction(
            @PathVariable String transactionId) {

        return transactionService.getTransaction(transactionId);
    }
}