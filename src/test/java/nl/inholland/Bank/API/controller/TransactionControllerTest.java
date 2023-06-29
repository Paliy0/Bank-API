package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.Transaction;
import nl.inholland.Bank.API.model.TransactionType;
import nl.inholland.Bank.API.model.dto.TransactionRequestDTO;
import nl.inholland.Bank.API.model.dto.TransactionResponseDTO;
import nl.inholland.Bank.API.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        transactionService = mock(TransactionService.class);
        transactionController = new TransactionController(transactionService);
    }



    @Test
    void postTransaction_ValidTransaction_ReturnsCreatedResponse() {
        // Arrange
        TransactionRequestDTO requestDTO = new TransactionRequestDTO();
        Transaction transaction = new Transaction();
        TransactionResponseDTO expectedResponse = new TransactionResponseDTO();

        TransactionService transactionService = Mockito.mock(TransactionService.class);
        when(transactionService.performTransaction(requestDTO)).thenReturn(transaction);

        TransactionController transactionController = new TransactionController(transactionService);

        // Act
        ResponseEntity<Object> responseEntity = transactionController.postTransaction(requestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());

        verify(transactionService, times(1)).performTransaction(requestDTO);
    }


    @Test
    void postTransaction_NullTransactionData_ReturnsUnprocessableEntityResponse() {
        TransactionRequestDTO requestDTO = null;

        TransactionService transactionService = Mockito.mock(TransactionService.class);

        TransactionController transactionController = new TransactionController(transactionService);

        ResponseEntity<Object> responseEntity = transactionController.postTransaction(requestDTO);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        assertEquals("Transaction data is missing or null.", responseEntity.getBody());

        verify(transactionService, never()).performTransaction(any());
    }



    @Test
    void postTransaction() {
    }

    @Test
    void getTransactionById() {
    }

    @Test
    void performDeposit() {
    }

    @Test
    void performWithdrawal() {
    }

    @Test
    void getDailyTotal() {
    }

    @Test
    void buildTransactionResponse() {
    }
}