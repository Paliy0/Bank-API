package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.Transaction;
import nl.inholland.Bank.API.model.TransactionType;
import nl.inholland.Bank.API.model.dto.TransactionRequestDTO;
import nl.inholland.Bank.API.model.dto.TransactionResponseDTO;
import nl.inholland.Bank.API.service.TransactionService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping(value = "/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllTransactions(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) String fromIban,
            @RequestParam(required = false) String toIban,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
            ) {

        Page<Transaction> transactions = transactionService.getAllTransactions(userId, startDate, endDate, minAmount,
                maxAmount, transactionType, fromIban, toIban, page, size);

        List<TransactionResponseDTO> responses = new ArrayList<>();
        for (Transaction transaction : transactions) {
            responses.add(buildTransactionResponse(transaction));
        }

        return ResponseEntity.status(200).body(responses);
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> postTransaction(@RequestBody TransactionRequestDTO dto) {
        try {
            if (dto != null) {
                Transaction transaction = transactionService.performTransaction(dto);
                TransactionResponseDTO response = buildTransactionResponse(transaction);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Transaction data is missing or null.");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", errorMessage);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTransactionById(@PathVariable long id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            TransactionResponseDTO response = buildTransactionResponse(transaction);
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
    @PostMapping(value = "/atm/deposit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> performDeposit(@RequestBody TransactionRequestDTO dto) {
        try {
            if (dto != null) {
                Transaction deposit = transactionService.performDeposit(dto);
                TransactionResponseDTO response = buildTransactionResponse(deposit);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Deposit data is missing or null.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
    @PostMapping(value = "/atm/withdrawal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> performWithdrawal(@RequestBody TransactionRequestDTO dto) {
        try {
            if (dto != null) {
                Transaction withdrawal = transactionService.performWithdrawal(dto);
                TransactionResponseDTO response = buildTransactionResponse(withdrawal);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Withdrawal data is missing or null.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
    @GetMapping(value = "/getDailyTotal/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getDailyTotal(@PathVariable Long userId) {
        try {
            List<Transaction> userTransactionsToday = transactionService.getUserTransactionsByDay(userId, LocalDate.now());
            if (!userTransactionsToday.isEmpty()) {
                List<TransactionResponseDTO> responses = new ArrayList<>();
                for (Transaction transaction : userTransactionsToday) {
                    responses.add(buildTransactionResponse(transaction));
                }
                return ResponseEntity.status(200).body(responses);
            }
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public TransactionResponseDTO buildTransactionResponse(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getUser().getFirstName() + " " + transaction.getUser().getLastName(),
                transaction.getFromAccount().getIban(),
                transaction.getToAccount().getIban(),
                transaction.getAmount(),
                transaction.getTimestamp(),
                transaction.getDescription(),
                transaction.getTransactionType()
        );
    }
}