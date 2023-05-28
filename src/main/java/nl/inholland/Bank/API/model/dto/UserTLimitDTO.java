package nl.inholland.Bank.API.model.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class UserTLimitDTO {
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @Id
    private Long id;

    private String transactionLimit;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionLimit() {
        return this.transactionLimit;
    }

    public void setTransactionLimit(String transactionLimit) {
        this.transactionLimit = transactionLimit;
    }
}