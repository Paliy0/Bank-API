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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AccountResponseDTO{");
        sb.append("id=").append(id);
        sb.append(", iban='").append(iban).append('\'');
        sb.append(", balance='").append(balance).append('\'');
        sb.append(", absoluteLimit='").append(absoluteLimit).append('\'');
        sb.append(", createdAt=").append(createdAt).append('\'');
        sb.append(", accountType='").append(accountType).append('\'');
        sb.append(", accountStatus=").append(accountStatus).append('\'');
        sb.append(", accountHolder=").append(user.toString());
        sb.append('}');
        return sb.toString();
    }
}
