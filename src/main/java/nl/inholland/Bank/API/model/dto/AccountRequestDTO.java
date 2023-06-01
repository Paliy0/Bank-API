package nl.inholland.Bank.API.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.model.AccountType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountRequestDTO {
    private AccountStatus accountStatus;
    private AccountType accountType;
    private AccountUserResponseDTO accountHolder;
}
