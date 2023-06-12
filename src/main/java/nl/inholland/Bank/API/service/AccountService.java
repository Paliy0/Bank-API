package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.model.AccountType;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.*;
import nl.inholland.Bank.API.repository.AccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.modelMapper = new ModelMapper();
    }

    public List<AccountResponseDTO> getAllAccounts(int limit, int offset) {
        Iterable<Account> accountList = accountRepository.findAllByIbanNot("NL01INHO0000000001");
        int count = 0;
        List<AccountResponseDTO> result = new ArrayList<>();

        for (Account account : accountList) {

            Optional<User> userOptional = userService.getUserById(account.getAccountHolder().getId());
            AccountResponseDTO responseDTO = modelMapper.map(account, AccountResponseDTO.class);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                AccountUserResponseDTO accountUserResponseDTO = modelMapper.map(user, AccountUserResponseDTO.class);
                responseDTO.setUser(accountUserResponseDTO);
            }

            if (count >= offset && count < offset + limit) {
                result.add(responseDTO);
            }
            count++;
        }
        return result;
    }

    public List<MyAccountResponseDTO> findAccountsByLoggedInUser(Long id) {
        Iterable<Account> accounts = accountRepository.findAccountsByAccountHolder_Id(id);
        List<MyAccountResponseDTO> responseDTOS = new ArrayList<>();

        for (Account account : accounts) {
            MyAccountResponseDTO responseDTO = modelMapper.map(account, MyAccountResponseDTO.class);
            responseDTOS.add(responseDTO);
        }

        // Calculate combined balance for each user
        for (MyAccountResponseDTO responseDTO : responseDTOS) {
            double combinedBalance = 0.0;

            for (Account account : accounts) {
                combinedBalance += account.getBalance();
            }
            responseDTO.setTotalBalance(combinedBalance);
        }
        return responseDTOS;
    }

    public List<FindAccountResponseDTO> getIbanByCustomerName(String firstName) {
        Iterable<Account> accounts = accountRepository.findIbanByAccountHolder_FirstName(firstName);
        List<FindAccountResponseDTO> responseDTOS = new ArrayList<>();

        for (Account account : accounts) {
            FindAccountResponseDTO responseDTO = modelMapper.map(account, FindAccountResponseDTO.class);
            responseDTO.setUser(account.getAccountHolder().getFirstName() + " " + account.getAccountHolder().getLastName());
            responseDTOS.add(responseDTO);
        }

        return responseDTOS;
    }

    public AccountResponseDTO getAccountByIban2(String iban) {
        AccountResponseDTO responseDTO = modelMapper.map(accountRepository.findAccountByIbanAndIbanNot(iban, "NL01INHO0000000001"), AccountResponseDTO.class);

        Optional<User> userOptional = userService.getUserById(accountRepository.findAccountByIbanAndIbanNot(iban, "NL01INHO0000000001").getAccountHolder().getId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            AccountUserResponseDTO accountUserResponseDTO = modelMapper.map(user, AccountUserResponseDTO.class);
            responseDTO.setUser(accountUserResponseDTO);
        }
        return responseDTO;
    }

    public ResponseEntity<String> createAccount(AccountRequestDTO accountRequest) {
        Long userId = accountRequest.getAccountHolder().getId();
        boolean hasAccount = this.hasAccount(userId);
        AccountType accountType = accountRequest.getAccountType();

        if (hasAccount) {
            boolean hasCurrentAccount = this.hasCurrentAccount(userId, AccountType.CURRENT);
            boolean hasSavingsAccount = this.hasCurrentAccount(userId, AccountType.SAVINGS);

            if (hasCurrentAccount && hasSavingsAccount) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot add a new account, user already has both a savings and a current account");
            }

            switch (accountType) {
                case SAVINGS:
                    if (hasSavingsAccount) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot create another savings account");
                    } else if (!hasCurrentAccount) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot create a savings account without a current account");
                    }
                    break;

                case CURRENT:
                    if (hasCurrentAccount) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot create another current account");
                    } else if (hasSavingsAccount) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot create a current account when a savings account already exists");
                    }
                    break;

                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid account type");
            }
        } else {
            if (accountType.equals(AccountType.SAVINGS)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot create a savings account without an existing current account");
            }
        }

        Account account = modelMapper.map(accountRequest, Account.class);
        this.saveAccount(account);
        Account createdAccount = this.getAccountByIban(account.getIban());

        return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully: " + this.mapDto(createdAccount).toString());
    }

    public AccountResponseDTO updateAccountStatus(String iban, AccountStatus accountStatus) {
        Account updateAccountStatus = accountRepository.findAccountByIbanAndIbanNot(iban, "NL01INHO0000000001");
        updateAccountStatus.setAccountStatus(accountStatus);
        accountRepository.save(updateAccountStatus);

        return this.mapDto(updateAccountStatus);
    }

    public AccountResponseDTO updateAccountAbsoluteLimit(String iban, double absoluteLimit) {
        Account updateAccount = accountRepository.findAccountByIbanAndIbanNot(iban, "NL01INHO0000000001");
        updateAccount.setAbsoluteLimit(absoluteLimit);
        accountRepository.save(updateAccount);

        return this.mapDto(updateAccount);
    }

    public void saveAccount(Account newAccount) {
        if (newAccount.getIban() == null) {
            newAccount.setIban(generateIBAN());
        }
        accountRepository.save(newAccount);
    }

    ////////////////////////////////////////////HELPERS/////////////////////////////////////////////////////////////////

    public Account getAccountByIban(String iban) {
        return accountRepository.findAccountByIban(iban);
    }

    public Iterable<Account> findByAccountHolder(User user) {
        return accountRepository.findAccountsByAccountHolder(user);
    }

    public boolean hasAccount(Long id) {
        return accountRepository.existsAccountByAccountHolder_Id(id);
    }

    public boolean hasCurrentAccount(Long id, AccountType accountType) {
        return accountRepository.existsAccountByAccountHolder_IdAndAccountTypeEquals(id, accountType);
    }

    public Long countAccounts(Long id) {
        return accountRepository.countAccountByAccountHolder_Id(id);
    }

    private AccountResponseDTO mapDto(Account updateAccount) {
        AccountResponseDTO accountResponseDTO = modelMapper.map(updateAccount, AccountResponseDTO.class);
        Optional<User> userOptional = userService.getUserById(updateAccount.getAccountHolder().getId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            AccountUserResponseDTO accountUserResponseDTO = modelMapper.map(user, AccountUserResponseDTO.class);
            accountResponseDTO.setUser(accountUserResponseDTO);
        }

        return accountResponseDTO;
    }

    ////////////////////////////////////////////////GENERATE IBAN///////////////////////////////////////////////////////

    public String generateIBAN() {
        String iban;
        String countryCode = "NL";
        String bankCode = "INHO";

        do {
            String accountNumber = generateRandomAccountNumber();

            iban = countryCode + "00" + bankCode + "0" + accountNumber;
            iban = countryCode + calculateCheckDigits(iban) + bankCode + "0" + accountNumber;
        } while (accountRepository.existsAccountByIbanEquals(iban));

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
        return String.format("%02d", checkDigits);
    }
}
