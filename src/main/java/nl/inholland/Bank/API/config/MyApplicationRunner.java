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
        user.setFirstName("sasa");
        user.setLastName("crow");
        user.setPassword("password");
        user.setEmail("sasacrow@gmail.com");
        user.setBirthdate("14 may");
        user.setStreetName("schoonzichtlaan");
        user.setHouseNumber(8);
        user.setZipCode("2015 CL");
        user.setCity("Haarlem");
        user.setCountry("NL");
        user.setDailyLimit(100);
        user.setTransactionLimit(100);
        user.setRole(Role.ROLE_USER);
        userService.add(user);

        Account account = new Account(AccountType.CURRENT, AccountStatus.OPEN, user);
        account.setIban("NL06INHL0123456789");
        accountService.saveAccount(account);
    }
}
