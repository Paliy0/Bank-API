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

        User user2 = new User();
        user2.setFirstName("Alba");
        user2.setLastName("Placeres");
        user2.setEmail("inholland2@gmail.com");
        user2.setPassword("Test321!");
        user2.setBsn("987654321");
        user2.setPhoneNumber("+31123456789");
        user2.setBirthdate("2003-12-09");
        user2.setStreetName("Schoonzichtlaan");
        user2.setHouseNumber(162);
        user2.setZipCode("2015 CV");
        user2.setCity("Haarlem");
        user2.setCountry("Netherlands");
        user2.setDailyLimit(1500);
        user2.setTransactionLimit(120);
        user2.setRole(Role.ROLE_USER);
        userService.add(user2);

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

        Account user2CurrentAccount = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, user2);
        user2CurrentAccount.setIban(accountService.generateIBAN());
        user2CurrentAccount.setBalance(370.15);
        accountService.saveAccount(userCurrentAccount);

        Account user2SavingsAccount = new Account(AccountType.SAVINGS, AccountStatus.ACTIVE, user2);
        user2SavingsAccount.setIban(accountService.generateIBAN());
        user2SavingsAccount.setBalance(349.25);
        accountService.saveAccount(userSavingsAccount);

        Transaction transaction1 = new Transaction();
        transaction1.setTimestamp(LocalDateTime.now());
        transaction1.setFromAccount(userCurrentAccount);
        transaction1.setToAccount(user2CurrentAccount);
        transaction1.setAmount(50.0);
        transaction1.setDescription("user 1 to user 2");

        Transaction transaction2 = new Transaction();
        transaction2.setTimestamp(LocalDateTime.now());
        transaction2.setFromAccount(userCurrentAccount);
        transaction2.setToAccount(userSavingsAccount);
        transaction2.setAmount(25.0);
        transaction2.setDescription("user 1 current to user 1 savings");

        Transaction transaction3 = new Transaction();
        transaction3.setTimestamp(LocalDateTime.now());
        transaction3.setFromAccount(user2SavingsAccount);
        transaction3.setToAccount(userCurrentAccount);
        transaction3.setAmount(37.0);
        transaction3.setDescription("user 2 savings to user 2 current");

        Transaction transaction4 = new Transaction();
        transaction4.setTimestamp(LocalDateTime.of(2023, 06, 01, 14, 40));
        transaction4.setFromAccount(user2CurrentAccount);
        transaction4.setToAccount(userCurrentAccount);
        transaction4.setAmount(20.0);
        transaction4.setDescription("different day transaction user 2 to user 1");

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
        transactionRepository.save(transaction4);
    }
}