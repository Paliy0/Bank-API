package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Get all accounts
     * HTTP Method: GET
     * URL: /accounts
     */
    @GetMapping
    public ResponseEntity<Iterable<Account>> getAllAccounts() {
        try {
            //add limit and offset
            return ResponseEntity.ok().body(accountService.getAllAccounts());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get an account by IBAN
     * HTTP Method: GET
     * URL: /accounts/{iban}
     */
    @GetMapping(value = "/{iban}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccountByIban(@PathVariable String iban) {
        try {
            Account account = accountService.getAccountByIban(iban);

            if (account != null) {
                return ResponseEntity.ok().body(account);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with IBAN: " + iban + " not found");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving account: " + e.getMessage());
        }
    }

    /**
     * Get an IBAN by Customer Name
     * HTTP Method: GET
     * URL: /accounts/getIbanByCustomerName?firstName=accountHolderFirstName
     */
    @GetMapping(value = "/getIbanByCustomerName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Account>> getIbanByCustomerName(@RequestParam String firstName) {
        try {
            Iterable<Account> account = accountService.getIbanByCustomerName(firstName);

            if (account != null) {
                return ResponseEntity.ok().body(accountService.getIbanByCustomerName(firstName));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Post an account
     * HTTP Method: POST
     * URL: /accounts
     */
    @PostMapping
    public ResponseEntity<?> insertAccount(@RequestBody Account newAccount) {
        try {
//            if (!isLoggedInAsEmployee()) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
//            }
            accountService.saveAccount(newAccount);
            return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected server error");
        }
    }

    /**
     * Update status of account
     * HTTP Method: PUT
     * URL: /accounts/accountStatus/{iban}
     */
    @PutMapping(value = "/accountStatus/{iban}")
    public ResponseEntity<?> updateAccountStatus(@PathVariable String iban, @RequestBody AccountStatus accountStatus) {
        try {
            accountService.updateAccountStatus(iban, accountStatus);
            return ResponseEntity.status(HttpStatus.OK).body("Updated successfully");
            //return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected server error");
        }
    }
}
