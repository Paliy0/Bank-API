package nl.inholland.Bank.API.model.dto;

import nl.inholland.Bank.API.model.AccountStatus;

//this should be used for /accountStatus/iban
public record StatusAccountRequestDTO(AccountStatus accountStatus) {
}
