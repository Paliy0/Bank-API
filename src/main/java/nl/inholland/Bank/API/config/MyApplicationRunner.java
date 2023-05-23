package nl.inholland.Bank.API.config;

import jakarta.transaction.Transactional;
import nl.inholland.Bank.API.model.AccountType;
import nl.inholland.Bank.API.service.AccountService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountStatus;

import java.util.List;

@Component
@Transactional
public class MyApplicationRunner implements ApplicationRunner {

    private final AccountService accountService;

    public MyApplicationRunner(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Account account = new Account();
        account.setAbsoluteLimit(0);
        account.setBalance(0);
        account.setIban("NL06INHL0123456789");
        account.setAccountTypes(List.of(AccountType.CURRENT));
        account.setAccountStatuses(List.of(AccountStatus.OPEN));
        accountService.saveAccount(account);
    }
}
