package nl.inholland.Bank.API.model.dto;

public record TransactionRequestDTO(String fromIban, String toIban, Double amount, String description) {
}
