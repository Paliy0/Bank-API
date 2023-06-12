package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.model.*;
import nl.inholland.Bank.API.model.dto.TransactionRequestDTO;
import nl.inholland.Bank.API.repository.TransactionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final AccountService accountService;
    private final String bankIban = "NL01INHO0000000001";

    public TransactionService(TransactionRepository transactionRepository, @Lazy AccountService accountService, @Lazy UserService userService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.userService = userService;
    }

    public List<Transaction> getAllTransactions(int page, int limit, Long userId, LocalDate startDate,
                                                LocalDate endDate, Double minAmount, Double maxAmount,
                                                TransactionType transactionType){
        // Pagination
        Pageable pageable = PageRequest.of(page, limit);

        User user = null;

        if (userId != null){
            user = userService.getUserById(userId).orElse(null);
        }

        return transactionRepository.findTransactions(minAmount, maxAmount, user,
                transactionType, startDate, endDate, pageable).getContent();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public Transaction performTransaction(TransactionRequestDTO dto) {
        Transaction transaction = mapTransactionRequestDTOToTransaction(dto);
        transaction.setFromAccount(accountService.getAccountByIban(dto.fromIban()));
        transaction.setToAccount(accountService.getAccountByIban(dto.toIban()));
        int dailyLimit = 0;
        int transactionLimit = 0;

        // check that the accounts exist
        if (transaction.getFromAccount() != null || transaction.getToAccount() != null){
            transaction.setUser(transaction.getFromAccount().getAccountHolder());
            transaction.setTransactionType(TransactionType.TRANSACTION);
            dailyLimit = transaction.getUser().getDailyLimit();
            transactionLimit = transaction.getUser().getTransactionLimit();
        } else{
            throw new IllegalArgumentException("Invalid transaction: One or both of the accounts do not exist");
        }

        // check that both accounts are not the same
        if (transaction.getFromAccount().equals(transaction.getToAccount())) {
            throw new IllegalArgumentException("Invalid transaction: You can not make a transaction to your the same account");
        }

        // check if the transaction is from or to a savings account and if the user is the same
        if ((transaction.getFromAccount().getAccountType() == AccountType.SAVINGS || transaction.getToAccount().getAccountType() == AccountType.SAVINGS) && transaction.getFromAccount().getAccountHolder() != transaction.getToAccount().getAccountHolder()) {
            throw new IllegalArgumentException("Invalid transaction: You can only make transactions to or from your own savings account");
        }
        // check if the balance is going to become lower than the absolute limit
        if ((transaction.getFromAccount().getBalance() - transaction.getAmount()) < transaction.getFromAccount().getAbsoluteLimit()) {
            throw new IllegalArgumentException("Invalid transaction: Your balance cannot become lower than the absolute limit(" + transaction.getFromAccount().getAbsoluteLimit() + ")+");
        }
        // check if the daily limit is already reached
        if (dailyLimit > transaction.getUser().getDailyLimit()) {
            throw new IllegalArgumentException("Invalid transaction: You already reached your daily limit(" + dailyLimit + ")");
        }
        // check if the amount of the transaction is higher than the transaction limit
        if (transactionLimit < transaction.getAmount()) {
            throw new IllegalArgumentException("Invalid transaction: You are cannot transfer a higher amount than your transaction limit(" + transactionLimit + ")");
        }

        return transactionRepository.save(transaction);
    }

    public Transaction performDeposit(TransactionRequestDTO dto) {
        Transaction deposit = mapTransactionRequestDTOToTransaction(dto);
        deposit.setFromAccount(accountService.getAccountByIban(bankIban));
        deposit.setToAccount(accountService.getAccountByIban(dto.toIban()));

        if (deposit.getToAccount() != null){
            if (deposit.getToAccount().getAccountType() == AccountType.CURRENT){
                deposit.setUser(deposit.getToAccount().getAccountHolder());
                deposit.setTransactionType(TransactionType.DEPOSIT);
                return transactionRepository.save(deposit);
            } else {
                throw new IllegalArgumentException("Invalid deposit: You cannot deposit money to a savings account");
            }
        } else {
            throw new IllegalArgumentException("Invalid deposit: The account you are trying to deposit money to doesn't exist");
        }
    }

    public Transaction performWithdrawal(TransactionRequestDTO dto) {
        Transaction withdrawal = mapTransactionRequestDTOToTransaction(dto);
        withdrawal.setFromAccount(accountService.getAccountByIban(dto.fromIban()));
        withdrawal.setToAccount(accountService.getAccountByIban(bankIban));

        if (withdrawal.getFromAccount() != null){
            if (withdrawal.getFromAccount().getAccountType() == AccountType.CURRENT){
                withdrawal.setUser(withdrawal.getFromAccount().getAccountHolder());
                withdrawal.setTransactionType(TransactionType.WITHDRAWAL);
                return transactionRepository.save(withdrawal);
            } else{
                throw new IllegalArgumentException("Invalid withdrawal: You cannot withdraw money from a savings account");
            }
        } else {
            throw new IllegalArgumentException("Invalid withdrawal: The account you are trying to withdraw money from doesn't exist");
        }
    }

    public List<Transaction> getUserTransactionsByDay(Long userId, LocalDate day) {
        LocalDateTime startTime = day.atStartOfDay();
        LocalDateTime endTime = LocalDateTime.of(day, LocalTime.MAX);

        return transactionRepository.findTransactionsByUserIdAndTimestampBetween(userId, startTime, endTime);
    }

    private Transaction mapTransactionRequestDTOToTransaction(TransactionRequestDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        return transaction;
    }
}
