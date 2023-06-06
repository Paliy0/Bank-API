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
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        User bankUser = new User();
        bankUser.setFirstName("BANK");
        bankUser.setLastName("INHOLLAND");
        bankUser.setPassword("SecretPassword123!");
        bankUser.setEmail("bank@inholland.com");
        bankUser.setBsn("914815121");
        bankUser.setPhoneNumber("+31 914815121");
        bankUser.setBirthdate("2000-01-01");
        bankUser.setStreetName("Bijdorplaan");
        bankUser.setHouseNumber(15);
        bankUser.setZipCode("2015 CE");
        bankUser.setCity("Haarlem");
        bankUser.setCountry("Netherlands");
        bankUser.setDailyLimit(1000000);
        bankUser.setTransactionLimit(1000000);
        bankUser.setRole(Role.ROLE_EMPLOYEE);
        userService.add(bankUser);

        User user1 = new User();
        user1.setFirstName("Sasa");
        user1.setLastName("Crow");
        user1.setPassword("Test123!");
        user1.setEmail("inholland1@gmail.com");
        user1.setBsn("123455789");
        user1.setPhoneNumber("+31654557890");
        user1.setBirthdate("1990-07-20");
        user1.setStreetName("Schoonzichtlaan");
        user1.setHouseNumber(218);
        user1.setZipCode("2015 CL");
        user1.setCity("Haarlem");
        user1.setCountry("Netherlands");
        user1.setDailyLimit(1000);
        user1.setTransactionLimit(100);
        user1.setRole(Role.ROLE_USER);
        userService.add(user1);

        boolean hasAccount = false;
        userService.getAllUsers(hasAccount).forEach(System.out::println);

        Account bankAccount = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, bankUser);
        bankAccount.setIban("NL01INHO0000000001");
        bankAccount.setBalance(1000000);
        accountService.saveAccount(bankAccount);

        Account userCurrentAccount = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, user1);
        userCurrentAccount.setIban(accountService.generateIBAN());
        userCurrentAccount.setBalance(250.25);
        accountService.saveAccount(userCurrentAccount);

        Account userSavingsAccount = new Account(AccountType.SAVINGS, AccountStatus.ACTIVE, user1);
        userSavingsAccount.setIban(accountService.generateIBAN());
        userSavingsAccount.setBalance(249.75);
        accountService.saveAccount(userSavingsAccount);

        Transaction transaction1 = new Transaction();
        transaction1.setTimestamp(LocalDateTime.now());
        transaction1.setFromAccountIban("NL47INGB1234567890"); //select an actual account not a string iban
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