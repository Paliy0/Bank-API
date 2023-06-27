package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.model.dto.*;
import nl.inholland.Bank.API.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Get all accounts with pagination
     * HTTP Method: GET
     * URL: /accounts
     * Query parameters:
     * - limit - items per page (10 by default)
     * - offset - starting point (0 by default)
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts(@RequestParam(defaultValue = "10") int limit,
                                                                   @RequestParam(defaultValue = "0") int offset) {
        try {
            if (!accountService.getAllAccounts(limit, offset).isEmpty()) {
                return ResponseEntity.ok().body(accountService.getAllAccounts(limit, offset));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get accounts for logged-in user
     * HTTP Method: GET
     * URL: /accounts/myAccounts/{userId}
     */
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @GetMapping(value = "/myAccounts/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getMyAccounts(@PathVariable Long userId) {
        try {
            List<MyAccountResponseDTO> accountResponseDTOS = accountService.findAccountsByLoggedInUser(userId);
            double totalBalance = accountService.getTotalBalance(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("accounts", accountResponseDTOS);
            response.put("totalBalance", totalBalance);

            if (!accountResponseDTOS.isEmpty()) {
                return ResponseEntity.ok().body(response);
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
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @GetMapping(value = "/getIbanByCustomerName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<FindAccountResponseDTO>> getIbanByCustomerName(@RequestParam String firstName) {
        try {
            if (!accountService.getIbanByCustomerName(firstName).isEmpty()) {
                return ResponseEntity.ok().body(accountService.getIbanByCustomerName(firstName));
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
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @GetMapping(value = "/{iban}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccountByIban(@PathVariable String iban) {
        try {
            return ResponseEntity.ok().body(accountService.getAccountByIbanExceptBank(iban));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving account: Account with this IBAN does not exist");
        }
    }

    /**
     * Post an account
     * HTTP Method: POST
     * URL: /accounts
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping
    public ResponseEntity<Object> insertAccount(@RequestBody AccountRequestDTO accountRequest) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(accountRequest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    /**
     * Update status of account
     * HTTP Method: PUT
     * URL: /accounts/accountStatus/{iban}
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PutMapping(value = "/accountStatus/{iban}")
    public ResponseEntity<AccountResponseDTO> updateAccountStatus(@PathVariable String iban, @RequestBody StatusAccountRequestDTO accountStatusRequest) {
        try {
            AccountStatus newAccountStatus = AccountStatus.valueOf(accountStatusRequest.accountStatus());
            return ResponseEntity.status(HttpStatus.OK).body(accountService.updateAccountStatus(iban, newAccountStatus));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Update absoluteLimit of account
     * HTTP Method: PUT
     * URL: /accounts/absoluteLimit/{iban}
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PutMapping(value = "/absoluteLimit/{iban}")
    public ResponseEntity<AccountResponseDTO> updateAccountAbsoluteLimit(@PathVariable String iban, @RequestBody AbsoluteLimitAccountRequestDTO accountAbsoluteLimitRequest) {
        try {
            double absoluteLimit = accountAbsoluteLimitRequest.absoluteLimit();
            return ResponseEntity.status(HttpStatus.OK).body(accountService.updateAccountAbsoluteLimit(iban, absoluteLimit));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
