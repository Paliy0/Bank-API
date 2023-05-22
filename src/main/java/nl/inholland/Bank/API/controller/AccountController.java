package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.service.AccountService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
            return ResponseEntity.ok().body(accountService.getAllAccounts());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "{id}/value")
    public ResponseEntity getAccountById(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(accountService.getAccountById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
