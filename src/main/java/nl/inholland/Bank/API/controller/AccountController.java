package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.service.AccountService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
}
