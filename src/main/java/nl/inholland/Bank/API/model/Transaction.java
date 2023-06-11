package nl.inholland.Bank.API.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Transaction {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    @Id
    @Column
    private Long id;

    @Column
    private LocalDateTime timestamp;

    @OneToOne
    @Nullable
    private Account fromAccount;
    @OneToOne
    @Nullable
    private Account toAccount;

    @Column
    private Double amount;

    @Column
    private String description;

    @ManyToOne
    @PrimaryKeyJoinColumn
    private User user;

    @Column
    private TransactionType transactionType;

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
        sb.append(", transactionType=").append(transactionType).append('\'');
        sb.append('}');
        return sb.toString();
    }
    
}