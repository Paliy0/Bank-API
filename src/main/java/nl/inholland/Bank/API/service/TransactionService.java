package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.repository.TransactionRepository;
import nl.inholland.Bank.API.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;  

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Iterable<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public void performTransaction(Transaction newTransaction) {
        transactionRepository.save(newTransaction);
    }

    public List<Transaction> getUserTransactionsByDay(Long userId, LocalDateTime date){
        return transactionRepository.findTransactionByUserIdAndTimestamp(userId, date);
    }
}
