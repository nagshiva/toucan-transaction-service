package com.example.transactionstarter.service;

import org.springframework.stereotype.Service;

import com.example.transactionstarter.dto.TransactionRequest;
import com.example.transactionstarter.exception.DuplicateTransactionException;
import com.example.transactionstarter.exception.TransactionNotFoundException;
import com.example.transactionstarter.model.Transaction;
import com.example.transactionstarter.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(TransactionRequest request) {

        if (transactionRepository.existsById(request.getTransactionId())) {
            throw new DuplicateTransactionException(
                    "Transaction already exists: " + request.getTransactionId());
        }

        Transaction transaction = new Transaction(
                request.getTransactionId(),
                request.getCustomerId(),
                request.getAmount(),
                request.getCurrency().toUpperCase(),
                request.getTransactionType(),
                request.getTransactionStatus()
        );

        return transactionRepository.save(transaction);
    }

    public Transaction getTransaction(String transactionId) {

        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction not found: " + transactionId));
    }
}