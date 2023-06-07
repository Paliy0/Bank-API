package nl.inholland.Bank.API.repository;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long>{
    List<Transaction> findTransactionsByUserIdAndTimestampBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    List <Transaction> findTransactionsByUserIdAndTimestampBetweenAndFromAccount(Long userId, LocalDateTime startDate, LocalDateTime endDate, Account fromAccount);
    List <Transaction> findTransactionsByUserIdAndTimestampBetweenAndToAccount(Long userId, LocalDateTime startDate, LocalDateTime endDate, Account toAccount);
    List<Transaction> findTransactionsByUserIdAndAmountLessThan(Long userId, Double amount);
    List<Transaction> findTransactionsByUserIdAndAmountEquals(Long userId, Double amount);
    List<Transaction> findTransactionsByUserIdAndAmountGreaterThan(Long userId, Double amount);
}