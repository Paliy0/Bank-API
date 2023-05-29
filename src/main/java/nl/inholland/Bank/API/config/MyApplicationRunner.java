package nl.inholland.Bank.API.config;

import jakarta.transaction.Transactional;
import nl.inholland.Bank.API.model.*;
import nl.inholland.Bank.API.service.AccountService;
import nl.inholland.Bank.API.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class MyApplicationRunner implements ApplicationRunner {

    private final AccountService accountService;
    private final UserService userService;

    public MyApplicationRunner(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        User user = new User();
        user.setFirstName("Bank");
        user.setLastName("INHOLLAND");
        user.setPassword("password");
        user.setEmail("inholland@gmail.com");
        user.setBirthdate("01/01/2001");
        user.setStreetName("Bijdorplaan");
        user.setHouseNumber(15);
        user.setZipCode("2015 CE");
        user.setCity("Haarlem");
        user.setCountry("Netherlands");
        user.setDailyLimit(10000);
        user.setTransactionLimit(10000);
        user.setRole(Role.ROLE_EMPLOYEE);
        userService.add(user);

        Account account = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, user);
        //account.setIban(accountService.generateIBAN());
        account.setIban("NL01INHO0000000001");
        accountService.saveAccount(account);
    }
}
