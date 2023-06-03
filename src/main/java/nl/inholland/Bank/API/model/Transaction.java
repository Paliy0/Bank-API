package nl.inholland.Bank.API.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name="transactions")
public class Transaction {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    @Id
    private Long id;

    private LocalDateTime timestamp;
    private String fromAccountIban;
    private String toAccountIban;
    private int amount;
    private String description;
    private int userId;

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
    
    public String getFromAccountIban() {
        return fromAccountIban;
    }

    public void setFromAccountIban(String fromAccountIban) {
        this.fromAccountIban = fromAccountIban;
    }
    
    public String getToAccountIban() {
        return toAccountIban;
    }

    public void setToAccountIban(String toAccountIban) {
        this.toAccountIban = toAccountIban;
    }
    
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Transaction{");
        sb.append("id=").append(id);
        sb.append(", timestamp=").append(timestamp).append('\'');
        sb.append(", from=").append(fromAccountIban).append('\'');
        sb.append(", to=").append(toAccountIban).append('\'');
        sb.append(", amount=").append(amount).append('\'');
        sb.append(", description=").append(description).append('\'');
        sb.append(", userId=").append(userId).append('\'');
        sb.append('}');
        return sb.toString();
    }
    
}