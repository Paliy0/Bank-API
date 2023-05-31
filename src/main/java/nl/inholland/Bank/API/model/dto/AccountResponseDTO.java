package nl.inholland.Bank.API.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.model.AccountType;

import java.time.LocalDate;

//this should be used for /getAllAccounts and /getAccountByIBAN
//change user to userDTO
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountResponseDTO {
    private Long id;
    private String iban;
    private double balance;
    private double absoluteLimit;
    private LocalDate createdAt;
    private AccountType accountType;
    private AccountStatus accountStatus;
    private AccountUserResponseDTO user;
}
