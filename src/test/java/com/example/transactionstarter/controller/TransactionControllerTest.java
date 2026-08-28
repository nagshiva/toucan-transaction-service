package com.example.transactionstarter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateTransactionSuccessfully() throws Exception {

        String request = """
                {
                    "transactionId": "TEST1001",
                    "customerId": "CUS1001",
                    "amount": 2500.50,
                    "currency": "USD",
                    "transactionType": "PAYMENT",
                    "transactionStatus": "PENDING"
                }
                """;

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("TEST1001"))
                .andExpect(jsonPath("$.customerId").value("CUS1001"))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    void shouldRejectDuplicateTransaction() throws Exception {

        String request = """
                {
                    "transactionId": "TEST1002",
                    "customerId": "CUS1002",
                    "amount": 1000.00,
                    "currency": "USD",
                    "transactionType": "PAYMENT",
                    "transactionStatus": "PENDING"
                }
                """;

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void shouldRejectInvalidTransactionRequest() throws Exception {

        String request = """
                {
                    "transactionId": "",
                    "customerId": "",
                    "amount": -100,
                    "currency": "US",
                    "transactionType": "",
                    "transactionStatus": ""
                }
                """;

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.errors.transactionId").value(
                        "Transaction ID is required"))
                .andExpect(jsonPath("$.errors.customerId").value(
                        "Customer ID is required"))
                .andExpect(jsonPath("$.errors.currency").value(
                        "Currency must contain exactly 3 characters"));
    }
}