package nl.inholland.Bank.API.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="accounts")
public class Account {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @Id
    private Long id;

    private String iban;
    private int balance;
    private int absoluteLimit;
    private LocalDate createdAt = LocalDate.now();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<nl.inholland.Bank.API.model.AccountType> accountType;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<nl.inholland.Bank.API.model.AccountStatus> accountStatuse;

    //@ManyToOne
    //private User user;


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getAbsoluteLimit() {
        return absoluteLimit;
    }

    public void setAbsoluteLimit(int absoluteLimit) {
        this.absoluteLimit = absoluteLimit;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public List<AccountType> getAccountTypes() {
        return accountType;
    }

    public void setAccountTypes(List<AccountType> accountTypes) {
        this.accountType = accountTypes;
    }

    public List<AccountStatus> getAccountStatuses() {
        return accountStatuse;
    }

    public void setAccountStatuses(List<AccountStatus> accountStatuses) {
        this.accountStatuse = accountStatuses;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Account{");
        sb.append("id=").append(id);
        sb.append(", iban='").append(iban).append('\'');
        sb.append(", balance='").append(balance).append('\'');
        sb.append(", accountTypes='").append(accountType).append('\'');
        sb.append(", accountStatuses=").append(accountStatuse).append('\'');
        sb.append(", absoluteLimit=").append(absoluteLimit);
        sb.append('}');
        return sb.toString();
    }
}
