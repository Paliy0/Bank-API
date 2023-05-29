package nl.inholland.Bank.API.model.dto;

import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.model.AccountType;

public record AccountRequestDTO(AccountStatus accountStatus, AccountType accountType, Long id) {
}
