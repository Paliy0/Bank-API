package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.model.AccountType;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.*;
import nl.inholland.Bank.API.service.AccountService;
import nl.inholland.Bank.API.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")

@RequestMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {
    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.modelMapper = new ModelMapper();
        this.userService = userService;
    }

    /**
     * Get all accounts with pagination
     * HTTP Method: GET
     * URL: /accounts
     * Query parameters:
     * - limit - items per page (10 by default)
     * - offset - starting point (0 by default)
     */
    @GetMapping
    public ResponseEntity<Iterable<AccountResponseDTO>> getAllAccounts(@RequestParam(defaultValue = "10") int limit,
                                                                       @RequestParam(defaultValue = "0") int offset) {
        try {
            Iterable<Account> accounts = accountService.getAllAccounts(limit, offset);
            List<AccountResponseDTO> responseDTOS = new ArrayList<>();

            for (Account account : accounts) {
                Optional<User> userOptional = userService.getUserById(account.getAccountHolder().getId());
                AccountResponseDTO responseDTO = modelMapper.map(account, AccountResponseDTO.class);

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    AccountUserResponseDTO accountUserResponseDTO = modelMapper.map(user, AccountUserResponseDTO.class);
                    responseDTO.setUser(accountUserResponseDTO);
                }
                responseDTOS.add(responseDTO);
            }
            if (!responseDTOS.isEmpty()) {
                return ResponseEntity.ok().body(responseDTOS);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get an account by IBAN
     * HTTP Method: GET
     * URL: /accounts/{iban}
     */
    @GetMapping(value = "/{iban}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccountByIban(@PathVariable String iban) {
        try {
            Account account = accountService.getAccountByIban(iban);
            AccountResponseDTO responseDTO = modelMapper.map(account, AccountResponseDTO.class);

            Optional<User> userOptional = userService.getUserById(account.getAccountHolder().getId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                AccountUserResponseDTO accountUserResponseDTO = modelMapper.map(user, AccountUserResponseDTO.class);
                responseDTO.setUser(accountUserResponseDTO);
            }

            if (account.getIban().equals("NL01INHO0000000001")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Account with IBAN: " + iban + " is not accessible to anyone");
            }

            if (responseDTO != null) {
                return ResponseEntity.ok().body(responseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with IBAN: " + iban + " not found");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving account: " + e.getMessage());
        }
    }

    /**
     * Get accounts for logged-in user
     * HTTP Method: GET
     * URL: /accounts/myAccounts/{userId}
     */
    @GetMapping(value = "/myAccounts/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<MyAccountResponseDTO>> getMyAccounts(@PathVariable Long userId) {
        try {
            Iterable<Account> accounts = accountService.findAccountsByLoggedInUser(userId);
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

            if (!responseDTOS.isEmpty()) {
                return ResponseEntity.ok().body(responseDTOS);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get an IBAN by Customer Name
     * HTTP Method: GET
     * URL: /accounts/getIbanByCustomerName?firstName={accountHolderFirstName}
     */
    @GetMapping(value = "/getIbanByCustomerName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<FindAccountResponseDTO>> getIbanByCustomerName(@RequestParam String firstName) {
        try {
            Iterable<Account> accounts = accountService.getIbanByCustomerName(firstName);
            List<FindAccountResponseDTO> responseDTOS = new ArrayList<>();

            for (Account account : accounts) {
                FindAccountResponseDTO responseDTO = modelMapper.map(account, FindAccountResponseDTO.class);
                responseDTO.setUser(account.getAccountHolder().getFirstName() + " " + account.getAccountHolder().getLastName());
                responseDTOS.add(responseDTO);
            }
            if (!responseDTOS.isEmpty()) {
                return ResponseEntity.ok().body(responseDTOS);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Post an account
     * HTTP Method: POST
     * URL: /accounts
     */
    @PostMapping
    public ResponseEntity<String> insertAccount(@RequestBody AccountRequestDTO accountRequest) {
        try {
            Long userId = accountRequest.getAccountHolder().getId();
            boolean hasAccount = accountService.hasAccount(userId);
            AccountType accountType = accountRequest.getAccountType();

            if (hasAccount) {
                boolean hasCurrentAccount = accountService.hasCurrentAccount(userId, AccountType.CURRENT);
                boolean hasSavingsAccount = accountService.hasCurrentAccount(userId, AccountType.SAVINGS);

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
            accountService.saveAccount(account);
            Account createdAccount = accountService.getAccountByIban(account.getIban());

            AccountResponseDTO accountResponseDTO = modelMapper.map(createdAccount, AccountResponseDTO.class);
            Optional<User> userOptional = userService.getUserById(createdAccount.getAccountHolder().getId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                AccountUserResponseDTO accountUserResponseDTO = modelMapper.map(user, AccountUserResponseDTO.class);
                accountResponseDTO.setUser(accountUserResponseDTO);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully: " + accountResponseDTO.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected server error");
        }
    }

    /**
     * Update status of account
     * HTTP Method: PUT
     * URL: /accounts/accountStatus/{iban}
     */
    @PutMapping(value = "/accountStatus/{iban}")
    public ResponseEntity<String> updateAccountStatus(@PathVariable String iban, @RequestBody StatusAccountRequestDTO accountStatusRequest) {
        try {
            AccountStatus newAccountStatus = AccountStatus.valueOf(accountStatusRequest.accountStatus());
            accountService.updateAccountStatus(iban, newAccountStatus);
            Account createdAccount = accountService.getAccountByIban(iban);

            return ResponseEntity.status(HttpStatus.OK).body("Account status updated successfully: " + createdAccount.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected server error");
        }
    }

    /**
     * Update absoluteLimit of account
     * HTTP Method: PUT
     * URL: /accounts/absoluteLimit/{iban}
     */
    @PutMapping(value = "/absoluteLimit/{iban}")
    public ResponseEntity<String> updateAccountAbsoluteLimit(@PathVariable String iban, @RequestBody AbsoluteLimitAccountRequestDTO accountAbsoluteLimitRequest) {
        try {
            double absoluteLimit = accountAbsoluteLimitRequest.absoluteLimit();
            accountService.updateAccountAbsoluteLimit(iban, absoluteLimit);
            Account createdAccount = accountService.getAccountByIban(iban);

            return ResponseEntity.status(HttpStatus.OK).body("Absolute limit updated successfully: " + createdAccount.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected server error");
        }
    }
}
