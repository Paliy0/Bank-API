package nl.inholland.Bank.API.config;

import jakarta.transaction.Transactional;
import nl.inholland.Bank.API.model.*;
import nl.inholland.Bank.API.repository.TransactionRepository;
import nl.inholland.Bank.API.service.AccountService;
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
    private final TransactionRepository transactionRepository;

    public MyApplicationRunner(AccountService accountService, UserService userService, TransactionRepository transactionRepository) {
        this.accountService = accountService;
        this.userService = userService;
        this.transactionRepository = transactionRepository;

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        User user = new User();
        user.setFirstName("Bank");
        user.setLastName("INHOLLAND");
        user.setPassword("password");
        user.setEmail("sasacrow@gmail.com");
        user.setBsn("123456789");
        user.setPhoneNumber("+314567890");
        user.setBirthdate("2015-07-20");
        user.setStreetName("schoonzichtlaan");
        user.setHouseNumber(8);
        user.setZipCode("2015 CL");
        user.setCity("Haarlem");
        user.setCountry("Netherlands");
        user.setDailyLimit(10000);
        user.setTransactionLimit(10000);
        user.setRole(Role.ROLE_EMPLOYEE);
        userService.add(user);

        User user1 = new User();
        user1.setFirstName("Sasa");
        user1.setLastName("Crow");
        user1.setPassword("Test123!");
        user1.setEmail("inholland1@gmail.com");
        user1.setBsn("123455789");
        user1.setPhoneNumber("+314557890");
        user1.setBirthdate("2015-07-20");
        user1.setStreetName("Bijdorplaan");
        user1.setHouseNumber(15);
        user1.setZipCode("2015 CE");
        user1.setCity("Haarlem");
        user1.setCountry("Netherlands");
        user1.setDailyLimit(10000);
        user1.setTransactionLimit(10000);
        user1.setRole(Role.ROLE_USER);
        userService.add(user1);

        Account bankAccount = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, user);
        //account.setIban(accountService.generateIBAN());
        bankAccount.setIban("NL01INHO0000000001");
        bankAccount.setBalance(1000000);
        accountService.saveAccount(bankAccount);

        Account testAccount = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, user1);
        testAccount.setIban(accountService.generateIBAN());
        accountService.saveAccount(testAccount);

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