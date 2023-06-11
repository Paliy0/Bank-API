package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.Transaction;
import nl.inholland.Bank.API.model.TransactionType;
import nl.inholland.Bank.API.model.dto.TransactionRequestDTO;
import nl.inholland.Bank.API.model.dto.TransactionResponseDTO;
import nl.inholland.Bank.API.service.TransactionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllTransactions(
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Long> userId,
            @RequestParam Optional<LocalDate> startDate,
            @RequestParam Optional <LocalDate> endDate,
            @RequestParam Optional<Double> minAmount,
            @RequestParam Optional<Double> maxAmount,
            @RequestParam Optional<TransactionType> transactionType) {
        List<Transaction> transactions = transactionService.getAllTransactions(page.orElse(0), limit.orElse(10),
                userId.orElse(null), startDate.orElse(null), endDate.orElse(null), minAmount.orElse(0.00),
                maxAmount.orElse(Double.MAX_VALUE), transactionType.orElse(null));

        List<TransactionResponseDTO> responses = new ArrayList<>();
        for (Transaction transaction : transactions) {
            responses.add(buildTransactionResponse(transaction));
        }

        return ResponseEntity.status(200).body(responses);
    }

    @PostMapping
    public ResponseEntity<Object> postTransaction(@RequestBody TransactionRequestDTO dto) {
        try {
            if (dto != null){
                Transaction transaction = transactionService.performTransaction(dto);
                TransactionResponseDTO response = buildTransactionResponse(transaction);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else{
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Transaction data is missing or null.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTransactionById(@PathVariable long id){
        try{
            return ResponseEntity.ok().body(transactionService.getTransactionById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/atm/deposit")
    public ResponseEntity<?> performDeposit(@RequestBody TransactionRequestDTO dto) {
        try {
            if (dto != null){
                Transaction deposit = transactionService.performDeposit(dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(deposit);
            } else{
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Deposit data is missing or null.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping(value = "/atm/withdrawal")
    public ResponseEntity<?> performWithdrawal(@RequestBody TransactionRequestDTO dto) {
        try {
            if (dto != null){
                Transaction withdrawal = transactionService.performWithdrawal(dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(withdrawal);
            } else{
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Withdrawal data is missing or null.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/getDailyTotal/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Transaction>> getDailyTotal(@PathVariable Long userId){
        try{
            List<Transaction> userTransactionsToday = transactionService.getUserTransactionsByDay(userId, LocalDate.now());
            if(!userTransactionsToday.isEmpty()){
                return ResponseEntity.ok().body(userTransactionsToday);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private TransactionResponseDTO buildTransactionResponse(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getUser().getFirstName() + " " + transaction.getUser().getLastName(),
                transaction.getFromAccount().getIban(),
                transaction.getToAccount().getIban(),
                transaction.getAmount(),
                transaction.getTimestamp(),
                transaction.getDescription(),
                transaction.getTransactionType()
        );
    }
}