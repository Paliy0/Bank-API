package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.model.Role;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    public Iterable<Account> getAllAccounts(int limit, int offset) {
        Iterable<Account> accountList = accountRepository.findAll();
        int count = 0;
        List<Account> result = new ArrayList<>();
        for (Account account : accountList) {
            if (count >= offset && count < offset + limit) {
                result.add(account);
            }
            count++;
        }
        return result;
    }

    public Account getAccountByIban(String iban) {
        return accountRepository.findAccountByIban(iban);
    }

    public Iterable<Account> getIbanByCustomerName(String firstName) {
        return accountRepository.findIbanByAccountHolder_FirstName(firstName);
    }

    public void saveAccount(Account newAccount) {
        User accountHolder = newAccount.getAccountHolder();
        if(accountHolder.getRole().equals(Role.ROLE_USER)){
            accountHolder.setRole(Role.ROLE_CUSTOMER);
            userService.add(accountHolder);
        }
        accountRepository.save(newAccount);
    }

    public Iterable<Account> findByAccountHolder(User user) {
        return accountRepository.findAccountsByAccountHolder(user);
    }

    public void updateAccountStatus(String iban, AccountStatus accountStatus) {
        Account updateAccount = accountRepository.findAccountByIban(iban);
        updateAccount.setAccountStatus(accountStatus);
        accountRepository.save(updateAccount);
    }

    public String generateIBAN() {
        // Generate a random IBAN (Example: NL00INHO0123456789)
        String countryCode = "NL";
        String code = "00";
        String bankCode = "INHB";
        String accountNumber = generateRandomNumberString(10);
        return countryCode + code + bankCode + accountNumber;
    }

    private String generateRandomNumberString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }
}
