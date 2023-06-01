package nl.inholland.Bank.API.config;

import jakarta.transaction.Transactional;
import nl.inholland.Bank.API.model.*;
import nl.inholland.Bank.API.service.AccountService;
import nl.inholland.Bank.API.service.TransactionService;
import nl.inholland.Bank.API.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Transactional
public class MyApplicationRunner implements ApplicationRunner {

    private final AccountService accountService;
    private final UserService userService;
    private final TransactionService transactionService;

    public MyApplicationRunner(AccountService accountService, UserService userService, TransactionService transactionService) {
        this.accountService = accountService;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        User user = new User();
        user.setFirstName("sasa");
        user.setLastName("crow");
        user.setPassword("password");
        user.setEmail("sasacrow@gmail.com");
        user.setBsn("123456789");
        user.setPhoneNumber("+314567890");
        user.setBirthdate("2015-07-20");
        user.setStreetName("schoonzichtlaan");
        user.setHouseNumber(8);
        user.setZipCode("2015 CL");
        user.setCity("Haarlem");
        user.setCountry("NL");
        user.setDailyLimit(100);
        user.setTransactionLimit(100);
        user.setRole(Role.ROLE_EMPLOYEE);
        userService.add(user);

        User user1 = new User();
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setPassword("secure123");
        user1.setEmail("johndoe@gmail.com");
        user1.setBsn("987654321");
        user1.setPhoneNumber("+19876543210");
        user1.setBirthdate("1990-05-15");
        user1.setStreetName("Main Street");
        user1.setHouseNumber(123);
        user1.setZipCode("12345");
        user1.setCity("New York");
        user1.setCountry("USA");
        user1.setDailyLimit(200);
        user1.setTransactionLimit(200);
        user1.setRole(Role.ROLE_USER);
        userService.add(user1);

        User user2 = new User();
        user2.setFirstName("Emma");
        user2.setLastName("Smith");
        user2.setPassword("password123");
        user2.setEmail("emma.smith@example.com");
        user2.setBsn("987654321");
        user2.setPhoneNumber("+447876543210");
        user2.setBirthdate("1985-10-25");
        user2.setStreetName("Oak Avenue");
        user2.setHouseNumber(45);
        user2.setZipCode("AB12 CD3");
        user2.setCity("London");
        user2.setCountry("UK");
        user2.setDailyLimit(150);
        user2.setTransactionLimit(150);
        user2.setRole(Role.ROLE_USER);
        userService.add(user2);

        Account account = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, user);
        account.setIban(accountService.generateIBAN());
        accountService.saveAccount(account);

        Account account1 = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, user1);
        account1.setIban(accountService.generateIBAN());
        accountService.saveAccount(account1);

        Transaction transaction1 = new Transaction();
        transaction1.setTimestamp(LocalDateTime.now());
        transaction1.setFromAccountIban("NL47INGB1234567890");
        transaction1.setToAccountIban("NL56ABNA0987654321");
        transaction1.setAmount(50);
        transaction1.setDescription("test transaction");
        transaction1.setUserId(1);

        Transaction transaction2 = new Transaction();
        transaction2.setTimestamp(LocalDateTime.now());
        transaction2.setFromAccountIban("NL91ABNA0417164300");
        transaction2.setToAccountIban("NL69RABO0123456789");
        transaction2.setAmount(25);
        transaction2.setDescription("some random transaction");
        transaction2.setUserId(2);

        transactionService.performTransaction(transaction1);
        transactionService.performTransaction(transaction2);
    }
}