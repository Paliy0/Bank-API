package nl.inholland.Bank.API.repository;

import nl.inholland.Bank.API.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long>{
    List<Transaction> findTransactionByUserIdAndTimestamp(Long userId, LocalDateTime date);
}
