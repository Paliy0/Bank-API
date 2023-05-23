package nl.inholland.Bank.API.repository;

import nl.inholland.Bank.API.model.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Long>{
    // Transaction findTransactionById(Long id);
}
