package nl.inholland.Bank.API.model.dto;

import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.model.AccountType;

//this should be used for creating an account
public record AccountRequestDTO(AccountStatus accountStatus, AccountType accountType, Long userId) {
}
