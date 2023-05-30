package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.model.AccountType;
import nl.inholland.Bank.API.model.dto.AccountRequestDTO;
import nl.inholland.Bank.API.model.dto.FindAccountResponseDTO;
import nl.inholland.Bank.API.model.dto.StatusAccountRequestDTO;
import nl.inholland.Bank.API.service.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {
    private final AccountService accountService;
    private final ModelMapper modelMapper;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
        this.modelMapper = new ModelMapper();
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
    public ResponseEntity<Iterable<Account>> getAllAccounts(@RequestParam(defaultValue = "10") int limit,
                                                            @RequestParam(defaultValue = "0") int offset) {
        try {
            return ResponseEntity.ok().body(accountService.getAllAccounts(limit, offset));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
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

            if (account.getIban().equals("NL01INHO0000000001")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Account with IBAN: " + iban + " is not accessible to anyone");
            }

            if (account != null) {
                return ResponseEntity.ok().body(account);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with IBAN: " + iban + " not found");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving account: " + e.getMessage());
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
                if (accountType.equals(AccountType.SAVINGS)) {
                    boolean hasCurrentAccount = accountService.hasCurrentAccount(userId, AccountType.CURRENT);
                    if (!hasCurrentAccount) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot create a savings account without a current account");
                    }
                } else if (accountType.equals(AccountType.CURRENT)) {
                    Long accountCount = accountService.countAccounts(userId);
                    if (accountCount >= 2) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot add a new account, user already has two accounts");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot add a new account, user already has two accounts");
                }
            }

            Account account = modelMapper.map(accountRequest, Account.class);
            accountService.saveAccount(account);
            Account createdAccount = accountService.getAccountByIban(account.getIban());

            return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully: " + createdAccount.toString());
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

            return ResponseEntity.status(HttpStatus.OK).body("Updated successfully: " + createdAccount.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected server error");
        }
    }
}
