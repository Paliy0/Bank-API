package nl.inholland.Bank.API.model.dto;
import java.time.LocalDateTime;

public record TransactionDTO(LocalDateTime timestamp, String fromAccountIban, String toAccountIban, int amount, String description, int userId) {

}
