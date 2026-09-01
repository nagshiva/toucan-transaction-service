package com.example.transactionstarter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.transactionstarter.model.Transaction;

public interface TransactionRepository
        extends JpaRepository<Transaction, String> {

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    List<Transaction> findByCustomerId(String customerId);
}