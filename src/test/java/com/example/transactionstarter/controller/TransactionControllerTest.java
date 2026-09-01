package com.example.transactionstarter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    // =========================================================
    // A. POST - CREATE TRANSACTION
    // =========================================================

    @Test
    void shouldCreateTransactionSuccessfully() throws Exception {

        String request = """
                {
                    "customerId": "CUS1001",
                    "amount": 2500.50,
                    "currency": "USD",
                    "transactionType": "PAYMENT"
                }
                """;

        mockMvc.perform(post("/api/transactions")
                .header("Idempotency-Key", "POST-TEST-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").isNotEmpty())
                .andExpect(jsonPath("$.customerId").value("CUS1001"))
                .andExpect(jsonPath("$.amount").value(2500.50))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.transactionType").value("PAYMENT"))
                .andExpect(jsonPath("$.transactionStatus").value("PENDING"));
    }


    @Test
    void shouldReturnSameTransactionForDuplicateIdempotencyKey()
            throws Exception {

        String request = """
                {
                    "customerId": "CUS1002",
                    "amount": 1000.00,
                    "currency": "USD",
                    "transactionType": "PAYMENT"
                }
                """;

        String firstResponse = mockMvc.perform(post("/api/transactions")
                .header("Idempotency-Key", "POST-TEST-002")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String secondResponse = mockMvc.perform(post("/api/transactions")
                .header("Idempotency-Key", "POST-TEST-002")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode first = objectMapper.readTree(firstResponse);
        JsonNode second = objectMapper.readTree(secondResponse);

        org.junit.jupiter.api.Assertions.assertEquals(
                first.get("transactionId").asText(),
                second.get("transactionId").asText());
    }


    @Test
    void shouldRejectInvalidTransactionRequest() throws Exception {

        String request = """
                {
                    "customerId": "",
                    "amount": -100,
                    "currency": "US",
                    "transactionType": null
                }
                """;

        mockMvc.perform(post("/api/transactions")
                .header("Idempotency-Key", "POST-TEST-003")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.errors.customerId").value(
                        "Customer ID is required"))
                .andExpect(jsonPath("$.errors.amount").value(
                        "Amount must be greater than zero"))
                .andExpect(jsonPath("$.errors.currency").value(
                        "Currency must be a valid ISO 4217 currency code"))
                .andExpect(jsonPath("$.errors.transactionType").value(
                        "Transaction type is required"));
    }


    @Test
    void shouldRejectInvalidTransactionType() throws Exception {

        String request = """
                {
                    "customerId": "CUS1004",
                    "amount": 100.00,
                    "currency": "USD",
                    "transactionType": "INVALID"
                }
                """;

        mockMvc.perform(post("/api/transactions")
                .header("Idempotency-Key", "POST-TEST-004")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Request"));
    }


    // =========================================================
    // B. GET - GET TRANSACTION
    // =========================================================

    @Test
    void shouldGetTransactionSuccessfully() throws Exception {

        String request = """
                {
                    "customerId": "CUS2001",
                    "amount": 1500.00,
                    "currency": "INR",
                    "transactionType": "PAYMENT"
                }
                """;

        String response = mockMvc.perform(post("/api/transactions")
                .header("Idempotency-Key", "GET-TEST-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode transaction = objectMapper.readTree(response);

        String transactionId =
                transaction.get("transactionId").asText();

        mockMvc.perform(
                get("/api/transactions/" + transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId")
                        .value(transactionId))
                .andExpect(jsonPath("$.customerId")
                        .value("CUS2001"))
                .andExpect(jsonPath("$.amount")
                        .value(1500.00))
                .andExpect(jsonPath("$.currency")
                        .value("INR"))
                .andExpect(jsonPath("$.transactionType")
                        .value("PAYMENT"))
                .andExpect(jsonPath("$.transactionStatus")
                        .value("PENDING"));
    }


    @Test
    void shouldReturnNotFoundForUnknownTransaction() throws Exception {

        String unknownTransactionId =
                "00000000-0000-0000-0000-000000000999";

        mockMvc.perform(
                get("/api/transactions/" + unknownTransactionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }


    // =========================================================
    // D. GET - GET CUSTOMER TRANSACTIONS
    // =========================================================

    @Test
    void shouldGetAllTransactionsForCustomer() throws Exception {

        String request1 = """
                {
                    "customerId": "CUS6001",
                    "amount": 1000.00,
                    "currency": "INR",
                    "transactionType": "PAYMENT"
                }
                """;

        String request2 = """
                {
                    "customerId": "CUS6001",
                    "amount": 2500.00,
                    "currency": "USD",
                    "transactionType": "TRANSFER"
                }
                """;

        mockMvc.perform(post("/api/transactions")
                .header("Idempotency-Key", "CUSTOMER-TEST-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/transactions")
                .header("Idempotency-Key", "CUSTOMER-TEST-002")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request2))
                .andExpect(status().isCreated());

        mockMvc.perform(
                get("/api/transactions/customer/CUS6001/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].customerId").value("CUS6001"))
                .andExpect(jsonPath("$[1].customerId").value("CUS6001"));
    }


    @Test
    void shouldReturnEmptyListForCustomerWithNoTransactions()
            throws Exception {

        mockMvc.perform(
                get("/api/transactions/customer/CUS9999/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    // =========================================================
    // C. PATCH - UPDATE TRANSACTION STATUS
    // =========================================================

    @Test
    void shouldUpdatePendingTransactionToCompleted()
            throws Exception {

        String transactionId =
                createPendingTransaction("PATCH-TEST-001");

        String request = """
                {
                    "status": "COMPLETED"
                }
                """;

        mockMvc.perform(
                patch("/api/transactions/"
                        + transactionId
                        + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId")
                        .value(transactionId))
                .andExpect(jsonPath("$.transactionStatus")
                        .value("COMPLETED"));
    }


    @Test
    void shouldUpdatePendingTransactionToFailed()
            throws Exception {

        String transactionId =
                createPendingTransaction("PATCH-TEST-002");

        String request = """
                {
                    "status": "FAILED"
                }
                """;

        mockMvc.perform(
                patch("/api/transactions/"
                        + transactionId
                        + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionStatus")
                        .value("FAILED"));
    }


    @Test
    void shouldUpdatePendingTransactionToCancelled()
            throws Exception {

        String transactionId =
                createPendingTransaction("PATCH-TEST-003");

        String request = """
                {
                    "status": "CANCELLED"
                }
                """;

        mockMvc.perform(
                patch("/api/transactions/"
                        + transactionId
                        + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionStatus")
                        .value("CANCELLED"));
    }


    @Test
    void shouldRejectCompletedToCancelled()
            throws Exception {

        String transactionId =
                createPendingTransaction("PATCH-TEST-004");

        updateStatus(transactionId, "COMPLETED");

        String request = """
                {
                    "status": "CANCELLED"
                }
                """;

        mockMvc.perform(
                patch("/api/transactions/"
                        + transactionId
                        + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error")
                        .value("Invalid Transaction Status"));
    }


    @Test
    void shouldRejectFailedToCompleted()
            throws Exception {

        String transactionId =
                createPendingTransaction("PATCH-TEST-005");

        updateStatus(transactionId, "FAILED");

        String request = """
                {
                    "status": "COMPLETED"
                }
                """;

        mockMvc.perform(
                patch("/api/transactions/"
                        + transactionId
                        + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error")
                        .value("Invalid Transaction Status"));
    }


    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownTransaction()
            throws Exception {

        String request = """
                {
                    "status": "COMPLETED"
                }
                """;

        mockMvc.perform(
                patch("/api/transactions/"
                        + "00000000-0000-0000-0000-000000000999"
                        + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }


    // =========================================================
    // TEST HELPERS
    // =========================================================

    private String createPendingTransaction(
            String idempotencyKey) throws Exception {

        String request = """
                {
                    "customerId": "CUS3001",
                    "amount": 2000.00,
                    "currency": "USD",
                    "transactionType": "PAYMENT"
                }
                """;

        String response = mockMvc.perform(post("/api/transactions")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode transaction =
                objectMapper.readTree(response);

        return transaction
                .get("transactionId")
                .asText();
    }


    private void updateStatus(
            String transactionId,
            String status) throws Exception {

        String request = """
                {
                    "status": "%s"
                }
                """.formatted(status);

        mockMvc.perform(
                patch("/api/transactions/"
                        + transactionId
                        + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }
}