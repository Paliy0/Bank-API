package nl.inholland.Bank.API.model.dto;

import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.model.AccountType;
import nl.inholland.Bank.API.model.User;

import java.time.LocalDate;

//this should be used for /getAllAccounts and /getAccountByIBAN
//change user to userDTO
public record AccountResponseDTO (String iban, double balance, double absoluteLimit, LocalDate createdAt, AccountType accountType, AccountStatus accountStatus,  User user) {

}
