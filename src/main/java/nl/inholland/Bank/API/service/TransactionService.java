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
    private final AccountService accountService;
    private final String bankIban = "NL01INHO0000000001";

    public TransactionService(TransactionRepository transactionRepository, @Lazy AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
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
        // map request to transaction
        Transaction transaction = mapTransactionRequestDTOToTransaction(dto);

        // get the accounts based on the ibans
        transaction.setFromAccount(accountService.getAccountByIban(dto.fromIban()));
        transaction.setToAccount(accountService.getAccountByIban(dto.toIban()));

        // check if the transaction is valid
        validateTransaction(transaction);

        // set missing transaction information
        transaction.setUser(transaction.getFromAccount().getAccountHolder());
        transaction.setTransactionType(TransactionType.TRANSACTION);

        // update accounts with new balances
        transaction.getFromAccount().setBalance(transaction.getFromAccount().getBalance() - transaction.getAmount());
        transaction.getToAccount().setBalance(transaction.getToAccount().getBalance() + transaction.getAmount());
        accountService.saveAccount(transaction.getFromAccount());
        accountService.saveAccount(transaction.getToAccount());

        // update daily limit just if its between different users
        if (transaction.getUser() != transaction.getToAccount().getAccountHolder())
            transaction.getUser().setDailyLimit((int) (transaction.getUser().getDailyLimit() - transaction.getAmount()));

        // save the transaction
        return transactionRepository.save(transaction);
    }

    public void validateTransaction(Transaction transaction) {
        // check that the accounts exist
        if (transaction.getFromAccount() == null || transaction.getToAccount() == null) {
            throw new IllegalArgumentException("One or both of the accounts do not exist");
        }

        // check that both accounts are not the same
        if (transaction.getFromAccount().equals(transaction.getToAccount())) {
            throw new IllegalArgumentException("You can not make a transaction to your same account");
        }

        // check if the transaction is between different users
        if (transaction.getFromAccount().getAccountHolder() != transaction.getToAccount().getAccountHolder()){
            // check if transaction limit is reached
            if (transaction.getFromAccount().getAccountHolder().getTransactionLimit() < transaction.getAmount()) {
                throw new IllegalArgumentException("The transaction amount is higher than your transaction limit");
            }
            // check if the daily limit is reached
            if (transaction.getFromAccount().getAccountHolder().getDailyLimit() < transaction.getAmount()) {
                throw new IllegalArgumentException("The transaction amount is higher than your daily limit");
            }
            // check if the transaction is not from or to a savings account
            if (transaction.getFromAccount().getAccountType() == AccountType.SAVINGS || transaction.getToAccount().getAccountType() == AccountType.SAVINGS) {
                throw new IllegalArgumentException("You can only transfer money between your own current and savings accounts");
            }
        }

        // check if the balance is going to become lower than the absolute limit
        if ((transaction.getFromAccount().getBalance() - transaction.getAmount()) < transaction.getFromAccount().getAbsoluteLimit()) {
            throw new IllegalArgumentException("Your balance cannot become lower than the absolute limit");
        }
    }

    public Transaction performDeposit(TransactionRequestDTO dto) {
        Transaction deposit = mapTransactionRequestDTOToTransaction(dto);

        // get the accounts based on the ibans
        deposit.setFromAccount(accountService.getAccountByIban(bankIban));
        deposit.setToAccount(accountService.getAccountByIban(dto.toIban()));

        // check if the deposit is valid
        validateATM(deposit);

        // set missing deposit information
        deposit.setUser(deposit.getToAccount().getAccountHolder());
        deposit.setTransactionType(TransactionType.DEPOSIT);

        // update account with new balance
        deposit.getToAccount().setBalance(deposit.getToAccount().getBalance() + deposit.getAmount());
        accountService.saveAccount(deposit.getToAccount());

        // save the deposit
        return transactionRepository.save(deposit);
    }

    public Transaction performWithdrawal(TransactionRequestDTO dto) {
        Transaction withdrawal = mapTransactionRequestDTOToTransaction(dto);

        // get the accounts based on the ibans
        withdrawal.setFromAccount(accountService.getAccountByIban(dto.fromIban()));
        withdrawal.setToAccount(accountService.getAccountByIban(bankIban));

        // check if the withdrawal is valid
        validateATM(withdrawal);

        // set missing withdrawal information
        withdrawal.setUser(withdrawal.getFromAccount().getAccountHolder());
        withdrawal.setTransactionType(TransactionType.WITHDRAWAL);

        // update account with new balance
        withdrawal.getFromAccount().setBalance(withdrawal.getFromAccount().getBalance() - withdrawal.getAmount());
        accountService.saveAccount(withdrawal.getFromAccount());

        // update daily limit
        withdrawal.getUser().setDailyLimit((int) (withdrawal.getUser().getDailyLimit() - withdrawal.getAmount()));

        // save the withdrawal
        return transactionRepository.save(withdrawal);
    }

    public void validateATM(Transaction atmTransaction){
        // check if the account exists
        if (atmTransaction.getFromAccount() == null || atmTransaction.getToAccount() == null) {
            throw new IllegalArgumentException("The account doesn't exist");
        }

        // check account type
        if (atmTransaction.getFromAccount().getAccountType() == AccountType.SAVINGS || atmTransaction.getToAccount().getAccountType() == AccountType.SAVINGS) {
            throw new IllegalArgumentException("You cannot withdraw or deposit money using your savings account");
        }

        // check if the balance is going to become lower than the absolute limit
        if ((atmTransaction.getFromAccount().getBalance() - atmTransaction.getAmount()) < (atmTransaction.getFromAccount().getAbsoluteLimit())){
            throw new IllegalArgumentException("The balance cannot become lower than the absolute limit");
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
