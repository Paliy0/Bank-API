package nl.inholland.Bank.API.model.dto;
import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.User;

import java.time.LocalDateTime;

public record TransactionDTO(LocalDateTime timestamp, Account fromAccount, Account toAccount, Double amount, String description, User user) {

}
