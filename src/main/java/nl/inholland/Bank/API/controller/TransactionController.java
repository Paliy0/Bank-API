package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.Transaction;
import nl.inholland.Bank.API.model.dto.TransactionDTO;
import nl.inholland.Bank.API.service.TransactionService;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {
    private final TransactionService transactionService;

    private final ModelMapper modelMapper;
    
    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
        modelMapper = new ModelMapper();
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping
    public ResponseEntity<Iterable<Transaction>> getAllTransactions() {
        try {
            return ResponseEntity.ok().body(transactionService.getAllTransactions());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
    @PostMapping
    public ResponseEntity<Object> postTransaction(@RequestBody TransactionDTO dto) {
        try {
            if (dto != null){
                Transaction transaction = transactionService.performTransaction(dto);
                return ResponseEntity.status(HttpStatus.CREATED).body("Transaction successful.");
            } else{
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Transaction data is missing or null.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTransactionById(@PathVariable long id){
        try{
            return ResponseEntity.ok().body(transactionService.getTransactionById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
    @PostMapping(value = "/atm/deposit")
    public ResponseEntity<?> performDeposit(@RequestBody TransactionDTO dto) {
        try {
            if (dto != null){
                Transaction deposit = transactionService.performDeposit(dto);
                return ResponseEntity.status(HttpStatus.CREATED).body("Deposit successful.");
            } else{
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Deposit data is missing or null.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
    @PostMapping(value = "/atm/withdrawal")
    public ResponseEntity<?> performWithdrawal(@RequestBody TransactionDTO dto) {
        try {
            if (dto != null){
                Transaction withdrawal = transactionService.performWithdrawal(dto);
                return ResponseEntity.status(HttpStatus.CREATED).body("Withdrawal successful.");
            } else{
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Withdrawal data is missing or null.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
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