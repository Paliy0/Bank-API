package nl.inholland.Bank.API.config;

import jakarta.transaction.Transactional;
import nl.inholland.Bank.API.model.*;
import nl.inholland.Bank.API.repository.TransactionRepository;
import nl.inholland.Bank.API.service.AccountService;
import nl.inholland.Bank.API.service.UserService;

import java.time.LocalDateTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class MyApplicationRunner implements ApplicationRunner {

    private final AccountService accountService;
    private final UserService userService;
    private final TransactionRepository transactionRepository;

    public MyApplicationRunner(AccountService accountService, UserService userService, TransactionRepository transactionRepository) {
        this.accountService = accountService;
        this.userService = userService;
        this.transactionRepository = transactionRepository;
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

        Account account = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, user);
        account.setIban(accountService.generateIBAN());
        accountService.saveAccount(account);

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

        Transaction transaction3 = new Transaction();
        transaction3.setTimestamp(LocalDateTime.now());
        transaction3.setFromAccountIban("NL47INGB1234567841");
        transaction3.setToAccountIban("NL56ABNA0987654352");
        transaction3.setAmount(37);
        transaction3.setDescription("another transaction");
        transaction3.setUserId(1);

        Transaction transaction4 = new Transaction();
        transaction4.setTimestamp(LocalDateTime.of(2023, 06, 01, 14, 40));
        transaction4.setFromAccountIban("NL47INGB1234567841");
        transaction4.setToAccountIban("NL56ABNA0987654352");
        transaction4.setAmount(20);
        transaction4.setDescription("different day transaction");
        transaction4.setUserId(1);

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
        transactionRepository.save(transaction4);
    }
}