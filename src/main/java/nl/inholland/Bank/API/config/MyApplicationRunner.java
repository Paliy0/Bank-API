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
        bankUser.setBsn("203514435");
        bankUser.setPhoneNumber("+31697389599");
        bankUser.setBirthdate("1980-01-01");
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
        customer.setEmail("customer@inholland.com");
        customer.setBsn("350876412");
        customer.setPhoneNumber("+31669519063");
        customer.setBirthdate("1999-05-14");
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
        employee.setFirstName("John");
        employee.setLastName("Weak");
        employee.setPassword("Test123!");
        employee.setEmail("employee@inholland.com");
        employee.setBsn("456744494");
        employee.setPhoneNumber("+31667628938");
        employee.setBirthdate("1990-07-20");
        employee.setStreetName("Nieuwe Hoogstraat");
        employee.setHouseNumber(14);
        employee.setZipCode("1011 HC");
        employee.setCity("Amsterdam");
        employee.setCountry("Netherlands");
        employee.setDailyLimit(10000);
        employee.setTransactionLimit(1000);
        employee.setRole(Role.ROLE_EMPLOYEE);
        userService.add(employee);

        User user2 = new User();
        user2.setFirstName("Jo");
        user2.setLastName("Becker");
        user2.setPassword("Test123!");
        user2.setEmail("user@inholland.com");
        user2.setBsn("882011107");
        user2.setPhoneNumber("+49015754702");
        user2.setBirthdate("2000-03-20");
        user2.setStreetName("Bergerweg");
        user2.setHouseNumber(111);
        user2.setZipCode("2041 AV");
        user2.setCity("Haarlem");
        user2.setCountry("Netherlands");
        user2.setDailyLimit(200000);
        user2.setTransactionLimit(100);
        user2.setRole(Role.ROLE_USER);
        userService.add(user2);

        User user3 = new User();
        user3.setFirstName("Alba");
        user3.setLastName("Placeres");
        user3.setEmail("inholland2@gmail.com");
        user3.setPassword("Test321!");
        user3.setBsn("987654321");
        user3.setPhoneNumber("+31123456789");
        user3.setBirthdate("2003-12-09");
        user3.setStreetName("Schoonzichtlaan");
        user3.setHouseNumber(162);
        user3.setZipCode("2015 CV");
        user3.setCity("Haarlem");
        user3.setCountry("Netherlands");
        user3.setDailyLimit(150000000);
        user3.setTransactionLimit(120);
        user3.setRole(Role.ROLE_USER);
        userService.add(user3);

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

        Account employeeCurrent = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, employee);
        employeeCurrent.setIban(accountService.generateIBAN());
        employeeCurrent.setBalance(2411.51);
        accountService.saveAccount(employeeCurrent);

        Account employeeSavings = new Account(AccountType.SAVINGS, AccountStatus.ACTIVE, employee);
        employeeSavings.setIban(accountService.generateIBAN());
        employeeSavings.setBalance(5000.00);
        accountService.saveAccount(employeeSavings);

        Transaction transaction1 = new Transaction();
        transaction1.setUser(customer);
        transaction1.setTimestamp(LocalDateTime.now());
        transaction1.setFromAccount(userCurrentAccount);
        transaction1.setToAccount(employeeSavings);
        transaction1.setAmount(50.0);
        transaction1.setDescription("customer to user 5");
        transaction1.setTransactionType(TransactionType.TRANSACTION);

        Transaction transaction2 = new Transaction();
        transaction2.setUser(customer);
        transaction2.setTimestamp(LocalDateTime.now());
        transaction2.setFromAccount(userCurrentAccount);
        transaction2.setToAccount(userSavingsAccount);
        transaction2.setAmount(25.0);
        transaction2.setDescription("customer current to customer savings");
        transaction2.setTransactionType(TransactionType.TRANSACTION);

        Transaction transaction3 = new Transaction();
        transaction3.setUser(employee);
        transaction3.setTimestamp(LocalDateTime.now());
        transaction3.setFromAccount(employeeSavings);
        transaction3.setToAccount(employeeSavings);
        transaction3.setAmount(37.0);
        transaction3.setDescription("user 5 savings to user 5 current");
        transaction3.setTransactionType(TransactionType.TRANSACTION);

        Transaction transaction4 = new Transaction();
        transaction4.setUser(employee);
        transaction4.setTimestamp(LocalDateTime.of(2023, 06, 01, 14, 40));
        transaction4.setFromAccount(employeeSavings);
        transaction4.setToAccount(userCurrentAccount);
        transaction4.setAmount(20.0);
        transaction4.setDescription("different day transaction user 5 to customer");
        transaction4.setTransactionType(TransactionType.TRANSACTION);

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
        transactionRepository.save(transaction4);
    }
}