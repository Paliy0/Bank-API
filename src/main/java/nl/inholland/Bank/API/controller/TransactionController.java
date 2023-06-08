package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.Transaction;
import nl.inholland.Bank.API.model.dto.TransactionDTO;
import nl.inholland.Bank.API.service.TransactionService;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {
    private final TransactionService transactionService;


    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

//    @GetMapping
//    public ResponseEntity<Iterable<Transaction>> getAllTransactions(@PathVariable Long userId,
//                                                                    @RequestParam Optional<Integer> page,
//                                                                    @RequestParam Optional<Integer> limit,
//                                                                    @RequestParam Optional<LocalDate> startDate,
//                                                                    @RequestParam Optional<LocalDate> endDate,
//                                                                    @RequestParam Optional<String> fromIban,
//                                                                    @RequestParam Optional<String> toIban,
//                                                                    @RequestParam Optional<Double> minAmount,
//                                                                    @RequestParam Optional<Double> maxAmount) {
//        try {
//            return ResponseEntity.ok().body(transactionService.getAllTransactions(userId, page, limit, startDate, endDate, fromIban, toIban, minAmount, maxAmount));
//        } catch (Exception e) {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @GetMapping
    public ResponseEntity<Iterable<Transaction>> getAllTransactions() {
        try {
            return ResponseEntity.ok().body(transactionService.getAllTransactions());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping
    public ResponseEntity<Object> postTransaction(@RequestBody TransactionDTO dto) {
        try {
            if (dto != null){
                transactionService.performTransaction(dto);
                return ResponseEntity.status(HttpStatus.CREATED).body("Transaction successful.");
            } else{
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Transaction data is missing or null.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
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
    public ResponseEntity<?> performDeposit(@RequestBody TransactionDTO dto) {
        try {
            if (dto != null){
                transactionService.performDeposit(dto);
                return ResponseEntity.status(HttpStatus.CREATED).body("Deposit successful.");
            } else{
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Deposit data is missing or null.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/atm/withdrawal")
    public ResponseEntity<?> performWithdrawal(@RequestBody TransactionDTO dto) {
        try {
            if (dto != null){
                transactionService.performWithdrawal(dto);
                return ResponseEntity.status(HttpStatus.CREATED).body("Withdrawal successful.");
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
}