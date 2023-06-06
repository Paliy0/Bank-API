package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountType;
import nl.inholland.Bank.API.model.dto.TransactionDTO;
import nl.inholland.Bank.API.repository.AccountRepository;
import nl.inholland.Bank.API.repository.TransactionRepository;
import nl.inholland.Bank.API.model.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final String bankIban = "NL01INHO0000000001";

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userService = userService;
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

        Account fromAccount = accountRepository.findAccountByIban(transaction.getFromAccountIban());
        Account toAccount = accountRepository.findAccountByIban(transaction.getToAccountIban());
        int dailyLimit = Integer.parseInt(userService.getDailyLimit(fromAccount.getId()).toString());
        int transactionLimit = Integer.parseInt(userService.getTransactionLimit(fromAccount.getId()).toString());

        // check if the transaction is from or to a savings account and if the user is the same
        if ((fromAccount.getAccountType() == AccountType.SAVINGS || toAccount.getAccountType() == AccountType.SAVINGS) && fromAccount.getAccountHolder().getId() != toAccount.getAccountHolder().getId()){
            throw new IllegalArgumentException("Invalid transaction: You can only make transactions to or from your own savings account");
        }
        // check if the balance is going to become lower than the absolute limit
        if ((fromAccount.getBalance() - transaction.getAmount()) < fromAccount.getAbsoluteLimit()) {
            throw new IllegalArgumentException("Invalid transaction: Your balance cannot become lower than the absolute limit(" + fromAccount.getAbsoluteLimit() + ")");
        }
        // check if the daily limit is already reached
        if (dailyLimit > fromAccount.getAbsoluteLimit()){
            throw new IllegalArgumentException("Invalid transaction: You already reached your daily limit(" + dailyLimit + ")");
        }
        // check if the amount of the transaction is higher than the transaction limit
        if (transactionLimit < transaction.getAmount()){
            throw new IllegalArgumentException("Invalid transaction: You are cannot transfer a higher amount than your transaction limit(" + transactionLimit + ")");
        }
        return transactionRepository.save(transaction);
    }

    public Transaction performDeposit(TransactionDTO dto) {
        Transaction deposit = this.mapDtoToTransaction(dto);
        deposit.setFromAccountIban(dto.fromAccountIban());
        deposit.setToAccountIban(bankIban);
        return transactionRepository.save(deposit);
    }

    public Transaction performWithdrawal(TransactionDTO dto) {
        Transaction withdrawal = this.mapDtoToTransaction(dto);
        withdrawal.setFromAccountIban(bankIban);
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
