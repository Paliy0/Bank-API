package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.model.*;
import nl.inholland.Bank.API.model.dto.TransactionRequestDTO;
import nl.inholland.Bank.API.repository.TransactionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
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

    public Page<Transaction> getAllTransactions(Long userId, LocalDate startDate, LocalDate endDate, Double minAmount, Double maxAmount,
                                                TransactionType transactionType, String fromIban, String toIban, Integer page, Integer size) {

        // Pagination
        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size);

        if (minAmount == null) {
            minAmount = 0.0;
        }

        if (maxAmount == null) {
            maxAmount = Double.MAX_VALUE;
        }

        return transactionRepository.findTransactions(userId, startDate, endDate, minAmount, maxAmount, transactionType, fromIban, toIban, pageable);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public Transaction performTransaction(TransactionRequestDTO dto) {
        Transaction transaction = mapTransactionRequestDTOToTransaction(dto);
        transaction.setFromAccount(accountService.getAccountByIban(dto.fromIban()));
        transaction.setToAccount(accountService.getAccountByIban(dto.toIban()));

        // check that the accounts exist
        if (transaction.getFromAccount() != null && transaction.getToAccount() != null) {

            transaction.setUser(transaction.getFromAccount().getAccountHolder());

            // check if the transaction is valid
            validateTransaction(transaction);

            transaction.setTransactionType(TransactionType.TRANSACTION);
            transaction.getFromAccount().setBalance(transaction.getFromAccount().getBalance() - transaction.getAmount());
            transaction.getToAccount().setBalance(transaction.getToAccount().getBalance() + transaction.getAmount());
            transaction.getUser().setDailyLimit((int) (transaction.getUser().getDailyLimit() - transaction.getAmount()));
            accountService.saveAccount(transaction.getFromAccount());
            accountService.saveAccount(transaction.getToAccount());
            return transactionRepository.save(transaction);
        }
        throw new IllegalArgumentException("One or both of the accounts do not exist");
    }

    public void validateTransaction(Transaction transaction) {
        // check if the transaction limit is reached
        if (transaction.getUser().getDailyLimit() < transaction.getAmount()) {
            throw new IllegalArgumentException("The transaction amount is higher than your daily limit");
        }

        // check if the daily limit is reached
        if (transaction.getUser().getTransactionLimit() < transaction.getAmount()) {
            throw new IllegalArgumentException("The transaction amount is higher than your transaction limit");
        }

        // check that both accounts are not the same
        if (transaction.getFromAccount().equals(transaction.getToAccount())) {
            throw new IllegalArgumentException("You can not make a transaction to your the same account");
        }

        // check if the transaction is from or to a savings account and if the user is the same
        if ((transaction.getFromAccount().getAccountType() == AccountType.SAVINGS || transaction.getToAccount().getAccountType() == AccountType.SAVINGS)
                && transaction.getFromAccount().getAccountHolder() != transaction.getToAccount().getAccountHolder()) {
            throw new IllegalArgumentException("You can only make transactions to or from your own savings account");
        }

        // check if the balance is going to become lower than the absolute limit
        if ((transaction.getFromAccount().getBalance() - transaction.getAmount()) < transaction.getFromAccount().getAbsoluteLimit()) {
            throw new IllegalArgumentException("Your balance cannot become lower than the absolute limit(" + transaction.getFromAccount().getAbsoluteLimit() + ")+");
        }
    }

    public Transaction performDeposit(TransactionRequestDTO dto) {
        Transaction deposit = mapTransactionRequestDTOToTransaction(dto);
        deposit.setFromAccount(accountService.getAccountByIban(bankIban));
        deposit.setToAccount(accountService.getAccountByIban(dto.toIban()));

        // check that account exists
        if (deposit.getToAccount() != null) {

            // check that the account type is current
            if (deposit.getToAccount().getAccountType() == AccountType.CURRENT) {
                deposit.setUser(deposit.getToAccount().getAccountHolder());
                deposit.getToAccount().setBalance(deposit.getToAccount().getBalance() + deposit.getAmount());
                accountService.saveAccount(deposit.getToAccount());
                return transactionRepository.save(deposit);
            }
            throw new IllegalArgumentException("You cannot deposit money to a savings account");
        }
        throw new IllegalArgumentException("The account you are trying to deposit money to doesn't exist");
    }

    public Transaction performWithdrawal(TransactionRequestDTO dto) {
        Transaction withdrawal = mapTransactionRequestDTOToTransaction(dto);
        withdrawal.setFromAccount(accountService.getAccountByIban(dto.fromIban()));
        withdrawal.setToAccount(accountService.getAccountByIban(bankIban));

        // check that account exists
        if (withdrawal.getFromAccount() != null) {

            // check that the account type is current
            if (withdrawal.getFromAccount().getAccountType() == AccountType.CURRENT) {
                withdrawal.setUser(withdrawal.getFromAccount().getAccountHolder());
                withdrawal.setTransactionType(TransactionType.WITHDRAWAL);

                // check if there is enough money in the account
                if (withdrawal.getAmount() < withdrawal.getFromAccount().getBalance()) {
                    withdrawal.getFromAccount().setBalance(withdrawal.getFromAccount().getBalance() - withdrawal.getAmount());
                    accountService.saveAccount(withdrawal.getFromAccount());
                    return transactionRepository.save(withdrawal);
                }
                throw new IllegalArgumentException("There is not enough money in your account");
            }
            throw new IllegalArgumentException("You cannot withdraw money from a savings account");
        }
        throw new IllegalArgumentException("The account you are trying to withdraw money from doesn't exist");
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
