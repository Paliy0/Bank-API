package nl.inholland.Bank.API.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @Id
    private Long id;
    private String iban;
    private double balance;
    private double absoluteLimit;
    private LocalDate createdAt = LocalDate.now();
    @Enumerated(EnumType.ORDINAL)
    private AccountType accountType;
    @Enumerated(EnumType.ORDINAL)
    private AccountStatus accountStatus;
    @ManyToOne (cascade = CascadeType.ALL)
    private User accountHolder;

    public Account(AccountType accountType, AccountStatus accountStatus, User accountHolder) {
        this.accountType = accountType;
        this.accountStatus = accountStatus;
        this.accountHolder = accountHolder;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Account{");
        sb.append("id=").append(id);
        sb.append(", iban='").append(iban).append('\'');
        sb.append(", balance='").append(balance).append('\'');
        sb.append(", absoluteLimit='").append(absoluteLimit).append('\'');
        sb.append(", createdAt=").append(createdAt).append('\'');
        sb.append(", accountType='").append(accountType).append('\'');
        sb.append(", accountStatus=").append(accountStatus).append('\'');
        sb.append(", accountHolder=").append(accountHolder);
        sb.append('}');
        return sb.toString();
    }
}
