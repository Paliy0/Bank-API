package nl.inholland.Bank.API.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="transactions")
public class Transaction {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    @Id
    private Long id;
    private LocalDateTime timestamp;
    @ManyToOne
    private Account fromAccount;
    @ManyToOne
    private Account toAccount;
    private Double amount;
    private String description;
    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Account getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }
    
    public Account getToAccount() {
        return toAccount;
    }

    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }
    
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() { return fromAccount.getAccountHolder(); }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Transaction{");
        sb.append("id=").append(id);
        sb.append(", timestamp=").append(timestamp).append('\'');
        sb.append(", from=").append(fromAccount).append('\'');
        sb.append(", to=").append(toAccount).append('\'');
        sb.append(", amount=").append(amount).append('\'');
        sb.append(", description=").append(description).append('\'');
        sb.append(", user=").append(user).append('\'');
        sb.append('}');
        return sb.toString();
    }
    
}