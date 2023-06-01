package nl.inholland.Bank.API.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "accounts")
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
    @ManyToOne
    private User accountHolder;

    public Account() {
    }

    public Account(AccountType accountType, AccountStatus accountStatus, User accountHolder) {
        this.accountType = accountType;
        this.accountStatus = accountStatus;
        this.accountHolder = accountHolder;
    }

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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getAbsoluteLimit() {
        return absoluteLimit;
    }

    public void setAbsoluteLimit(double absoluteLimit) {
        this.absoluteLimit = absoluteLimit;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public User getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(User accountHolder) {
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
