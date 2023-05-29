package nl.inholland.Bank.API.model.dto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionRequestDTO {
    private LocalDateTime timestamp;
    private String fromAccountIban;
    private String toAccountIban;
    private int amount;
    private String description;
    private int userId;

    public String getTimestamp() {
        DateTimeFormatter myDateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDatestamp = timestamp.format(myDateTimeFormatter);
        return formattedDatestamp;
    }

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
}
