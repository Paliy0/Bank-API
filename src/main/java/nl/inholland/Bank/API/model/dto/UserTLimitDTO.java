package nl.inholland.Bank.API.model.dto;

public record UserTLimitDTO (Long UserId, int transactionLimit) {
}