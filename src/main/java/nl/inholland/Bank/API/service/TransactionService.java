package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.repository.TransactionRepository;
import nl.inholland.Bank.API.model.Transaction;
import java.util.Optional;

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

    public void saveTransaction(Transaction newTransaction) {
        transactionRepository.save(newTransaction);
    }
}
