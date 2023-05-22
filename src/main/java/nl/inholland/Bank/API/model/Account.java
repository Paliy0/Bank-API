package nl.inholland.Bank.API.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="accounts")
public class Account {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @Id
    private Long id;

    private String iban;
    private int balance;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<nl.inholland.Bank.API.model.AccountType> accountTypes;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<nl.inholland.Bank.API.model.AccountStatus> accountStatuses;
    private int absoluteLimit;
    //fix the user
    private int user;

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

    public List<AccountType> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(List<AccountType> accountTypes) {
        this.accountTypes = accountTypes;
    }

    public List<AccountStatus> getAccountStatuses() {
        return accountStatuses;
    }

    public void setAccountStatuses(List<AccountStatus> accountStatuses) {
        this.accountStatuses = accountStatuses;
    }

    public int getAbsoluteLimit() {
        return absoluteLimit;
    }

    public void setAbsoluteLimit(int absoluteLimit) {
        this.absoluteLimit = absoluteLimit;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Account{");
        sb.append("id=").append(id);
        sb.append(", iban='").append(iban).append('\'');
        sb.append(", balance='").append(balance).append('\'');
        sb.append(", accountTypes='").append(accountTypes).append('\'');
        sb.append(", accountStatuses=").append(accountStatuses).append('\'');
        sb.append(", absoluteLimit=").append(absoluteLimit);
        sb.append('}');
        return sb.toString();
    }
}
