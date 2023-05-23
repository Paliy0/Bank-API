package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.Transaction;
import nl.inholland.Bank.API.service.TransactionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {
    private final TransactionService transactionService;
    
    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
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
    public ResponseEntity<Void> postTransaction(@RequestBody Transaction newTransaction) {
        try {
            transactionService.saveTransaction(newTransaction);
            return ResponseEntity.ok().build();
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

    // @PostMapping(value = "/atm/deposit")
    // public ResponseEntity<Void> postDeposit(@RequestBody ) {
    //     try {
            
    //     } catch (Exception e) {
    //     }
    // }

    // @PostMapping(value = "/atm/withdrawal")
    // public ResponseEntity<Void> postWithdrawal(@RequestBody ) {
    //     try {
            
    //     } catch (Exception e) {
    //     }
    // }

    // @GetMapping(value = "/getDailyTotal/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<?> getDailyTotal(){
    //     try{
    //     } catch (Exception e) {
    //         return ResponseEntity.notFound().build();
    //     }
    // }
}
