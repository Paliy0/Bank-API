package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
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
        //Iterable<Account> accountList = accountRepository.findAll();
        Iterable<Account> accountList = accountRepository.findAllByIbanNot("NL01INHO0000000001");
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
        if (newAccount.getIban() == null){
            newAccount.setIban(generateIBAN());
        }

        accountRepository.save(newAccount);
    }

    public void updateAccountStatus(String iban, AccountStatus accountStatus) {
        Account updateAccount = accountRepository.findAccountByIban(iban);
        updateAccount.setAccountStatus(accountStatus);
        accountRepository.save(updateAccount);
    }

//    public String generateIBAN() {
//        // Generate a random IBAN (Example: NLxxINHO0xxxxxxxxx)
//        Random random = new Random();
//        StringBuilder sb = new StringBuilder();
//        StringBuilder sb2 = new StringBuilder();
//        for (int i = 0; i < 2; i++) {
//            int code = random.nextInt(9);
//            sb.append(code);
//        }
//
//        for (int i = 0; i < 9; i++) {
//            int accountNumber = random.nextInt(9);
//            sb2.append(accountNumber);
//        }
//
//        String countryCode = "NL";
//        String code = sb.toString();
//        String bankCode = "INHO0";
//        String accountNumber = sb2.toString();
//
//        return countryCode + code + bankCode + accountNumber;
//    }

    public String generateIBAN() {
        String iban;
        String countryCode = "NL";
        String bankCode = "INHO";

        do {
            String accountNumber = generateRandomAccountNumber();

            iban = countryCode + "00" + bankCode + "0" + accountNumber;
            iban = countryCode + calculateCheckDigits(iban) + bankCode + "0" + accountNumber;
        } while(accountRepository.existsAccountByIbanEquals(iban));

        return iban;
    }

    private static String generateRandomAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            int accountNumber = random.nextInt(10);
            sb.append(accountNumber);
        }
        return sb.toString();
    }

    //chatGPT method following the official IBAN creation algorithm
    private static String calculateCheckDigits(String iban) {
        iban = iban.substring(4) + iban.substring(0, 2) + "00";

        // Convert letters to numbers (A = 10, B = 11, etc.)
        StringBuilder numericIBAN = new StringBuilder();
        for (int i = 0; i < iban.length(); i++) {
            char c = iban.charAt(i);
            if (Character.isDigit(c)) {
                numericIBAN.append(c);
            } else {
                int numericValue = Character.getNumericValue(c);
                numericIBAN.append(numericValue);
            }
        }

        // Perform mod-97 operation on the numeric IBAN
        BigInteger ibanNumber = new BigInteger(numericIBAN.toString());
        BigInteger mod97 = ibanNumber.mod(BigInteger.valueOf(97));
        int checkDigits = 98 - mod97.intValue();

        // Format the check digits to always have two digits
        String formattedCheckDigits = String.format("%02d", checkDigits);

        return formattedCheckDigits;
    }

}
