package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.model.dto.TransactionDTO;
import nl.inholland.Bank.API.repository.TransactionRepository;
import nl.inholland.Bank.API.model.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.engine.transaction.jta.platform.internal.WebSphereExtendedJtaPlatform;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final String atmAccount = "NL91ABNA0417164300";

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Iterable<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public Transaction performTransaction(TransactionDTO dto) {
        Transaction transaction = this.mapDtoToTransaction(dto);
        transaction.setFromAccountIban(dto.fromAccountIban());
        transaction.setToAccountIban(dto.toAccountIban());
        return transactionRepository.save(transaction);
    }

    public Transaction performDeposit(TransactionDTO dto) {
        Transaction deposit = this.mapDtoToTransaction(dto);
        deposit.setFromAccountIban(dto.fromAccountIban());
        deposit.setToAccountIban(atmAccount);
        return transactionRepository.save(deposit);
    }

    public Transaction performWithdrawal(TransactionDTO dto) {
        Transaction withdrawal = this.mapDtoToTransaction(dto);
        withdrawal.setFromAccountIban(atmAccount);
        withdrawal.setToAccountIban(dto.toAccountIban());
        return transactionRepository.save(withdrawal);
    }

    public List<Transaction> getUserTransactionsByDay(Long userId, LocalDate date){
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(23, 59, 59);
        return transactionRepository.findTransactionByUserIdAndTimestampBetween(userId, startDate, endDate);
    }

    private Transaction mapDtoToTransaction(TransactionDTO dto){
        Transaction transaction = new Transaction();
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        transaction.setUserId(dto.userId());
        return transaction;
    }
}
