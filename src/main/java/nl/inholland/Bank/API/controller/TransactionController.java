package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.Transaction;
import nl.inholland.Bank.API.model.dto.TransactionDTO;
import nl.inholland.Bank.API.service.TransactionService;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {
    private final TransactionService transactionService;

    private final ModelMapper modelMapper;
    
    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
        modelMapper = new ModelMapper();
    }

    @GetMapping
    public ResponseEntity<Iterable<Transaction>> getAllTransactions() {
        try {
            return ResponseEntity.ok().body(transactionService.getAllTransactions());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Object> postTransaction(@RequestBody TransactionDTO newTransaction) {
        try {
            Transaction transaction = modelMapper.map(newTransaction, Transaction.class);
            transactionService.performTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body("Transaction successful.");

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
    public ResponseEntity<?> performDeposit(@RequestBody Transaction newDeposit) {
        try {


            transactionService.performTransaction(newDeposit);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/atm/withdrawal")
    public ResponseEntity<?> performWithdrawal(@RequestBody Transaction newWithdrawal) {
        try {
            transactionService.performTransaction(newWithdrawal);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/getDailyTotal/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Transaction>> getDailyTotal(@PathVariable Long userId, @RequestBody LocalDateTime date){
        try{
            List<Transaction> transactionsByDay = transactionService.getUserTransactionsByDay(userId, date);
            if(!transactionsByDay.isEmpty()){
                return ResponseEntity.ok().body(transactionsByDay);
            } else {
                return ResponseEntity.noContent().build();
            }

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}