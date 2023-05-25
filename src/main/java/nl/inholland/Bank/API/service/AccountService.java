package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    public Iterable<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountByIban(String iban) {
        return accountRepository.findAccountByIban(iban);
    }

    public Iterable<Account> getIbanByCustomerName(String firstName) {
        return accountRepository.findIbanByAccountHolder_FirstName(firstName);
    }

    public void saveAccount(Account newAccount) {
        accountRepository.save(newAccount);
    }

    public void updateAccountStatus(String iban, AccountStatus accountStatus) {
        Account updateAccount = accountRepository.findAccountByIban(iban);
        updateAccount.setAccountStatus(accountStatus);
        accountRepository.save(updateAccount);
    }
}
