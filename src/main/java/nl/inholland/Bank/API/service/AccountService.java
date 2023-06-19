package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.config.MyAppProperties;
import nl.inholland.Bank.API.model.*;
import nl.inholland.Bank.API.model.dto.*;
import nl.inholland.Bank.API.repository.AccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final MyAppProperties appProperties;

    @Autowired
    public AccountService(@Qualifier("myAppProperties") MyAppProperties appProperties, AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.modelMapper = new ModelMapper();
        this.appProperties = appProperties;
    }

    public List<AccountResponseDTO> getAllAccounts(int limit, int offset) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Account> accountPage = accountRepository.findAllExceptBank(pageable, appProperties.getDefaultIban());

        List<AccountResponseDTO> result = new ArrayList<>();
        for (Account account : accountPage.getContent()) {
            AccountResponseDTO responseDTO = this.mapDto(account);
            result.add(responseDTO);
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

    public AccountResponseDTO getAccountByIbanExceptBank(String iban) {
        return this.mapDto(accountRepository.findAccountByIbanAndIbanNot(iban, appProperties.getDefaultIban()));
    }

    public AccountResponseDTO createAccount(AccountRequestDTO accountRequest) {
        Long userId = accountRequest.getAccountHolder().getId();
        User user = userService.getUserById(userId).get();
        AccountType accountType = accountRequest.getAccountType();

        if (this.hasAccount(accountRequest.getAccountHolder().getId())) {
            boolean hasCurrentAccount = this.hasCurrentAccount(accountRequest.getAccountHolder().getId(), AccountType.CURRENT);
            boolean hasSavingsAccount = this.hasCurrentAccount(accountRequest.getAccountHolder().getId(), AccountType.SAVINGS);

            if (hasCurrentAccount && hasSavingsAccount) {
                throw new IllegalArgumentException("Cannot add a new account, user already has both a savings and a current account");
            }
            switch (accountType) {
                case SAVINGS:
                    if (hasSavingsAccount) {
                        throw new IllegalArgumentException("Cannot create another savings account");
                    } else if (!hasCurrentAccount) {
                        throw new IllegalArgumentException("Cannot create a savings account without a current account");
                    }
                    break;

                case CURRENT:
                    if (hasCurrentAccount) {
                        throw new IllegalArgumentException("Cannot create another current account");
                    } else if (hasSavingsAccount) {
                        throw new IllegalArgumentException("Cannot create a current account when a savings account already exists");
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Invalid account type");
            }
        } else {
            if (accountType.equals(AccountType.SAVINGS)) {
                throw new IllegalArgumentException("Cannot create a savings account without an existing current account");
            }
        }

        Account account = modelMapper.map(accountRequest, Account.class);
        account.setAccountHolder(user);
        this.saveAccount(account);

        return this.mapDto(account);
    }

    public AccountResponseDTO updateAccountStatus(String iban, AccountStatus accountStatus) {
        Account updateAccountStatus = accountRepository.findAccountByIbanAndIbanNot(iban, appProperties.getDefaultIban());
        updateAccountStatus.setAccountStatus(accountStatus);
        accountRepository.save(updateAccountStatus);

        return this.mapDto(updateAccountStatus);
    }

    public AccountResponseDTO updateAccountAbsoluteLimit(String iban, double absoluteLimit) {
        Account updateAccountAbsoluteLimit = accountRepository.findAccountByIbanAndIbanNot(iban, appProperties.getDefaultIban());
        updateAccountAbsoluteLimit.setAbsoluteLimit(absoluteLimit);
        accountRepository.save(updateAccountAbsoluteLimit);

        return this.mapDto(updateAccountAbsoluteLimit);
    }

    public void saveAccount(Account newAccount) {
        if (newAccount.getIban() == null) {
            newAccount.setIban(generateIBAN());
        }
        User user = newAccount.getAccountHolder();

        if (Objects.equals(user.getRole().toString(), "ROLE_USER")) {
            user.setRole(Role.ROLE_CUSTOMER);
        }
        newAccount.setAccountHolder(user);
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

    public double getTotalBalance(Long id){
        return accountRepository.getCombinedBalanceByAccountHolderId(id);
    }

    private AccountResponseDTO mapDto(Account account) {
        AccountResponseDTO accountResponseDTO = modelMapper.map(account, AccountResponseDTO.class);
        User accountHolder = account.getAccountHolder();

        if (accountHolder != null) {
            AccountUserResponseDTO accountUserResponseDTO = modelMapper.map(accountHolder, AccountUserResponseDTO.class);
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
