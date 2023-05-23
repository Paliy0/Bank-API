package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequestMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<Iterable<Account>> getAllAccounts() {
        try {
            //add limit and offset
            return ResponseEntity.ok().body(accountService.getAllAccounts());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //change to search by iban
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccountById(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(accountService.getAccountById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity insertAccount(@RequestBody Account newAccount) {
        try {
            //check if user is logged in as employee
            accountService.saveAccount(newAccount);
            //URI location = new URI("/accounts" + newAccount.getId());
            return  ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected server error");
        }
    }

    @PutMapping("/{iban}/status")
    public ResponseEntity<Account> updateAccountStatus(@PathVariable("iban") String iban,
                                                       @RequestParam("status") String status) {
        try {

            //check user role
            //get account by iban
            //account = accountService.getAccountById(id);
            //if account is null - throw error
//            if (account == null) {
//                return ResponseEntity.notFound().build();
//            }
            //update status
            //save updated account
//            accountService.saveAccount(account);
//            return  ResponseEntity.ok().build(account);
        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected server error");
        }
        return null;
    }

}
