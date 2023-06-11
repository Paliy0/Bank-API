package nl.inholland.Bank.API.model.dto;

import nl.inholland.Bank.API.model.TransactionType;

import java.time.LocalDateTime;

public record TransactionResponseDTO(Long id, String userName, String fromIban, String toIban, double amount,
                                     LocalDateTime timestamp, String description, TransactionType transactionType) {
}
