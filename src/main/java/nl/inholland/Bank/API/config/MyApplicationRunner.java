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

        User customer = new User();
        customer.setFirstName("Sasa");
        customer.setLastName("Crow");
        customer.setPassword("Test123!");
        customer.setEmail("sasa@gmail.com");
        customer.setBsn("123455789");
        customer.setPhoneNumber("+31654557890");
        customer.setBirthdate("1990-07-20");
        customer.setStreetName("Schoonzichtlaan");
        customer.setHouseNumber(218);
        customer.setZipCode("2015 CL");
        customer.setCity("Haarlem");
        customer.setCountry("Netherlands");
        customer.setDailyLimit(1000);
        customer.setTransactionLimit(100);
        customer.setRole(Role.ROLE_CUSTOMER);
        userService.add(customer);
        
        User employee = new User();
        employee.setFirstName("employee");
        employee.setLastName("Crow");
        employee.setPassword("Test123!");
        employee.setEmail("employee@gmail.com");
        employee.setBsn("123455789");
        employee.setPhoneNumber("+31654557890");
        employee.setBirthdate("1990-07-20");
        employee.setStreetName("Schoonzichtlaan");
        employee.setHouseNumber(218);
        employee.setZipCode("2015 CL");
        employee.setCity("Haarlem");
        employee.setCountry("Netherlands");
        employee.setDailyLimit(1000);
        employee.setTransactionLimit(100);
        employee.setRole(Role.ROLE_EMPLOYEE);
        userService.add(employee);

        User user2 = new User();
        user2.setFirstName("Jo");
        user2.setLastName("Becker");
        user2.setPassword("Test123!");
        user2.setEmail("jo@student.nl");
        user2.setBsn("015754702");
        user2.setPhoneNumber("+49015754702");
        user2.setBirthdate("1990-07-20");
        user2.setStreetName("Schoonzichtlaan");
        user2.setHouseNumber(218);
        user2.setZipCode("2015 CL");
        user2.setCity("Haarlem");
        user2.setCountry("Netherlands");
        user2.setDailyLimit(200);
        user2.setTransactionLimit(100);
        user2.setRole(Role.ROLE_USER);
        userService.add(user2);

        boolean hasAccount = false;
        userService.getAllUsers(hasAccount, 0, 50).forEach(System.out::println);

        Account bankAccount = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, bankUser);
        bankAccount.setIban("NL01INHO0000000001");
        bankAccount.setBalance(1000000);
        accountService.saveAccount(bankAccount);

        Account userCurrentAccount = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, customer);
        userCurrentAccount.setIban(accountService.generateIBAN());
        userCurrentAccount.setBalance(250.25);
        accountService.saveAccount(userCurrentAccount);

        Account userSavingsAccount = new Account(AccountType.SAVINGS, AccountStatus.ACTIVE, customer);
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