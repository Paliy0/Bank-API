package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountType;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.TransactionDTO;
import nl.inholland.Bank.API.repository.AccountRepository;
import nl.inholland.Bank.API.repository.TransactionRepository;
import nl.inholland.Bank.API.model.Transaction;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final String bankIban = "NL01INHO0000000001";

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

//    public Iterable<Transaction> getAllTransactions(Long userId, Optional<Integer> page, Optional<Integer> limit, Optional<LocalDate> startDate, Optional<LocalDate> endDate, Optional<String> fromIban, Optional<String> toIban, Optional<Double> minAmount, Optional<Double> maxAmount){
//
//        // Pagination
//        int pageNumber = page.orElse(0);
//        int pageLimit = limit.orElse(10);
//        Pageable pageable = (Pageable) PageRequest.of(pageNumber, pageLimit);
//
//        // Filters
//        LocalDateTime startTime = startDate.orElse(LocalDate.now()).atStartOfDay();
//        LocalDateTime endTime = endDate.orElse(LocalDate.now()).atTime(23, 59, 59);
//
//        List<Transaction> filteredTransactions = new ArrayList<>();
//
//        if (startDate.isPresent() && endDate.isPresent()){
//            filteredTransactions.addAll(transactionRepository.findTransactionsByUserIdAndTimestampBetween(userId, startTime, endTime));
//        }
//
//        if (fromIban.isPresent()){
//            filteredTransactions.addAll(transactionRepository.findTransactionsByUserIdAndTimestampBetweenAndFromAccount(userId, startTime, endTime, accountRepository.findAccountByIban(fromIban.get())));
//        }
//
//        if (toIban.isPresent()){
//            filteredTransactions.addAll(transactionRepository.findTransactionsByUserIdAndTimestampBetweenAndToAccount(userId, startTime, endTime, accountRepository.findAccountByIban(toIban.get())));
//        }
//
//        if (minAmount != null ){
//            filteredTransactions.addAll(transactionRepository.findTransactionsByUserIdAndAmountGreaterThan(userId, minAmount.get()));
//        }
//
//        if (maxAmount != null){
//            filteredTransactions.addAll(transactionRepository.findTransactionsByUserIdAndAmountLessThan(userId, maxAmount.get()));
//        }
//
//        if (maxAmount.equals(minAmount)){
//            filteredTransactions.addAll(transactionRepository.findTransactionsByUserIdAndAmountEquals(userId, maxAmount.get()));
//        }
//
//        filteredTransactions = (List<Transaction>) transactionRepository.findAll();
//
//        return transactionRepository.findAll();
//    }

    public Iterable<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public Transaction performTransaction(TransactionDTO dto) {
        Transaction transaction = this.mapDtoToTransaction(dto);
        transaction.setFromAccount(dto.fromAccount());
        transaction.setToAccount(dto.toAccount());

        int dailyLimit = transaction.getFromAccount().getAccountHolder().getDailyLimit();
        int transactionLimit = transaction.getFromAccount().getAccountHolder().getTransactionLimit();

        // check that both accounts are not the same
        if (transaction.getFromAccount().equals(transaction.getToAccount())){
            throw new IllegalArgumentException("Invalid transaction: You can not make a transaction to your the same account");
        }

        // check if the transaction is from or to a savings account and if the user is the same
        if ((transaction.getFromAccount().getAccountType() == AccountType.SAVINGS || transaction.getToAccount().getAccountType() == AccountType.SAVINGS) && transaction.getFromAccount().getAccountHolder() != transaction.getToAccount().getAccountHolder()){
            throw new IllegalArgumentException("Invalid transaction: You can only make transactions to or from your own savings account");
        }
        // check if the balance is going to become lower than the absolute limit
        if ((transaction.getFromAccount().getBalance() - transaction.getAmount()) < transaction.getFromAccount().getAbsoluteLimit()) {
            throw new IllegalArgumentException("Invalid transaction: Your balance cannot become lower than the absolute limit(" + transaction.getFromAccount().getAbsoluteLimit() + ")+");
        }
        // check if the daily limit is already reached
        if (dailyLimit > transaction.getFromAccount().getAbsoluteLimit()){
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
        deposit.setFromAccount(dto.fromAccount());
        deposit.setToAccount(accountRepository.findAccountByIban(bankIban));
        return transactionRepository.save(deposit);
    }

    public Transaction performWithdrawal(TransactionDTO dto) {
        Transaction withdrawal = this.mapDtoToTransaction(dto);
        withdrawal.setFromAccount(accountRepository.findAccountByIban(bankIban));
        withdrawal.setToAccount(dto.toAccount());
        return transactionRepository.save(withdrawal);
    }

    public List<Transaction> getUserTransactionsByDay(Long userId, LocalDate date){
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(23, 59, 59);
        return transactionRepository.findTransactionsByUserIdAndTimestampBetween(userId, startDate, endDate);
    }

    private Transaction mapDtoToTransaction(TransactionDTO dto){
        Transaction transaction = new Transaction();
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        return transaction;
    }
}
