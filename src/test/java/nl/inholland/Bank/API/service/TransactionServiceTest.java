package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.filter.JwtTokenFilter;
import nl.inholland.Bank.API.model.*;
import nl.inholland.Bank.API.model.dto.TransactionRequestDTO;
import nl.inholland.Bank.API.repository.TransactionRepository;
import nl.inholland.Bank.API.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountService accountService;
    @InjectMocks
    private TransactionService transactionService;

    private List<Transaction> transactions;
    private List<User> users;
    private List<Account> accounts;
    private Account bankAccount;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    static final String EMPLOYEE_TOKEN = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJlbXBsb3llZUBpbmhvbGxhbmQuY29tIiwiYXV0aCI6WyJST0xFX0VNUExPWUVFIl0sImlhdCI6MTY4Nzg4NDcyMCwiZXhwIjoxNjg3OTcxMTIwfQ.fBVLu4JDv6kWawcEWXV3Bo9zjCtrE5x4-M9_Luk_Tx6LIYSGR5cwAe3XC9ZgihjEr4ciUuVN6MSZzH6dAS0nVArVtUI5bRMGdkpz3j95wEPtys1CUi2rlCujoWwuCptxQCgJ7nJk4tn_lroxXnG2sfo_cP-7Cp3HLDCCCZ4KJLP7S9JGoxynncbYHp6r52hfSbQ-SoB3uU8VBbno8LupY3cDX56hNC-EbvBYcrL99pfPy26Tu68Ts1-WMyBpK744_Cphx8SgbHDJdHP6LBqFWE9bkNnm9X9nFGUJcW15K2HJ5J91LNzc5drsLKON7uA4u59MWTOTvC_upiC2Z-e5xQ";
    static final String CUSTOMER_TOKEN = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJjdXN0b21lckBpbmhvbGxhbmQuY29tIiwiYXV0aCI6WyJST0xFX0NVU1RPTUVSIl0sImlhdCI6MTY4Nzg4ODU3OSwiZXhwIjoxNjg3OTc0OTc5fQ.ZLhBMQMs2RQuyHBRo-YGrDZFNoSUjIXOjXocsaZjw2Rp3UVVPiEv6y30LpLmvnnSOC8AJhxsLih4y1rYCN7595L88qml3n4Okk1q99-cRIWHDetaWi4IuScflSo-ewIsBPrTKosQFAg-SEp_AlCWLXcdSOJFdd_VB3Y5ZTNVMex4jMu45knHhC7zL0uW5NsjBgsHRcB31EcXV8y53vvHhzft8xL1rMbXPg_7sjJu3NSAXWdq-RgK0XYWAspKwHYvI78GUktGt8OA-GrxlxQ2_cXQ42RitN3A9u_HQdyLFTmYSr6DgboNduk004swGv1-veG7EBbkW-byY4JC0DqLVg";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionService = new TransactionService(transactionRepository, accountService);


        User bankUser = new User(1L, "BANK", "INHOLLAND", "SecretPassword123", "bank@inholland.com", "203514435", "" +
                "+31697389599", "1980-01-01", "Bijdorplaan", 15, "2015 CE", "Haarlem", "Netherlands",
                1000000, 1000000, Role.ROLE_EMPLOYEE);


        bankAccount = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, bankUser);
        bankAccount.setIban("NL01INHO0000000001");
        bankAccount.setBalance(1000000);

        // create users
        users = List.of(
                new User(2L, "Sasa", "Crow",  "customer@inholland.com", "Test123!", "350876412", "+31669519063", "1999-05-14",
                        "Schoonzichtlaan", 218, "2015 CL", "Haarlem", "Netherlands", 1000, 100, Role.ROLE_CUSTOMER),
                new User(3L, "John", "Weak", "employee@inholland.com", "Test123!","456744494", "+31667628938", "1990-07-20",
                        "Nieuwe Hoogstraat", 14, "1011 HC", "Amsterdam", "Netherlands", 10000, 1000, Role.ROLE_EMPLOYEE)
        );

        // create accounts
        accounts = List.of(
                new Account(2L, "NL01INHO0000000002", 100.00, 0.00,  LocalDate.now(), AccountType.CURRENT, AccountStatus.ACTIVE, users.get(0)),
                new Account(3L, "NL01INHO0000000003", 200.00, 0.00, LocalDate.now(), AccountType.SAVINGS, AccountStatus.ACTIVE, users.get(0)),
                new Account(4L, "NL01INHO0000000004", 300.00, 0.00, LocalDate.now(), AccountType.CURRENT, AccountStatus.ACTIVE, users.get(1)),
                new Account(5L, "NL01INHO0000000005", 400.00, 0.00, LocalDate.now(), AccountType.SAVINGS, AccountStatus.ACTIVE, users.get(1))
        );

        // create transactions
        transactions = List.of(
                new Transaction(1L, LocalDateTime.now(), accounts.get(0), accounts.get(2), 10.00, "current to current", users.get(0),TransactionType.TRANSACTION),
                new Transaction(2L, LocalDateTime.now(), accounts.get(0), accounts.get(1), 20.00, "current to savings same user", users.get(0),TransactionType.TRANSACTION),
                new Transaction(3L, LocalDateTime.now(), accounts.get(2), accounts.get(0), 30.00, "current to current ", users.get(0),TransactionType.TRANSACTION),
                new Transaction(4L, LocalDateTime.now(), bankAccount, accounts.get(0), 40.00, "deposit", users.get(0),TransactionType.DEPOSIT),
                new Transaction(5L, LocalDateTime.now(), accounts.get(0),bankAccount, 40.00, "withdrawal", users.get(0),TransactionType.WITHDRAWAL)
        );
    }

    @Test
    public void getAllTransactions_ReturnsAllTransactions() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 12, 31);
        Double minAmount = 0.0;
        Double maxAmount = Double.MAX_VALUE;
        TransactionType transactionType = TransactionType.TRANSACTION;
        String fromIban = "NL01INHO0000000002";
        String toIban = "NL01INHO0000000003";
        Integer page = 0;
        Integer size = 10;

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactionPage = new PageImpl<>(transactions, pageable, transactions.size());

        Mockito.when(transactionRepository.findTransactions(
                userId, startDate, endDate, minAmount, maxAmount, transactionType, fromIban, toIban, pageable))
                .thenReturn(transactionPage);

        Page<Transaction> result = transactionService.getAllTransactions(
                userId, startDate, endDate, minAmount, maxAmount, transactionType, fromIban, toIban, page, size);

        assertNotNull(result);
        assertEquals(transactions.size(), result.getContent().size());
        assertEquals(transactions.get(0), result.getContent().get(0));
        assertEquals(transactions.get(1), result.getContent().get(1));
    }

    @Test
    public void getTransactionById_ReturnsTransaction() {
        Long id = 1L;
        Transaction transaction = transactions.get(0);
        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransactionById(id);

        assertNotNull(result);
        assertEquals(transaction, result);
    }

    @Test
    public void performTransaction_ValidDto_ReturnsTransaction() {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO(accounts.get(0).getIban(), accounts.get(2).getIban(), 10.00, "current to current");
        Account fromAccount = accounts.get(0);
        Account toAccount = accounts.get(2);
        User user = users.get(0);

        when(accountService.getAccountByIban(requestDTO.fromIban())).thenReturn(fromAccount);
        when(accountService.getAccountByIban(requestDTO.toIban())).thenReturn(toAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transactions.get(0));

        Transaction result = transactionService.performTransaction(requestDTO);

        assertNotNull(result);
        assertEquals(transactions.get(0), result);
        assertEquals(fromAccount, result.getFromAccount());
        assertEquals(toAccount, result.getToAccount());
        assertEquals(user, result.getUser());
        assertEquals(TransactionType.TRANSACTION, result.getTransactionType());
    }

    @Test
    void validateTransaction_ValidTransaction_NoExceptionsThrown() {
        assertDoesNotThrow(() -> transactionService.validateTransaction(transactions.get(0)));
    }

    @Test
    void validateTransaction_NullFromAccount_ThrowsIllegalArgumentException() {
        Transaction transaction = transactions.get(0);
        transaction.setFromAccount(null);

        assertThrows(IllegalArgumentException.class, () -> transactionService.validateTransaction(transaction));
    }

    @Test
    void validateTransaction_NullToAccount_ThrowsIllegalArgumentException() {
        Transaction transaction = transactions.get(0);
        transaction.setToAccount(null);

        assertThrows(IllegalArgumentException.class, () -> transactionService.validateTransaction(transaction));
    }

    @Test
    void validateTransaction_SameAccounts_ThrowsIllegalArgumentException() {
        Transaction transaction = transactions.get(0);
        transaction.setToAccount(transaction.getFromAccount());

        assertThrows(IllegalArgumentException.class, () -> transactionService.validateTransaction(transaction));
    }

    @Test
    void validateTransaction_DifferentUsers_TransactionLimitsExceeded_ThrowsIllegalArgumentException() {
        Transaction transaction = transactions.get(0);
        User user = transaction.getUser();

        when(accountService.getAccountByIban(anyString())).thenReturn(transaction.getFromAccount());

        // Set transaction amount greater than the user's transaction limit
        transaction.setAmount(user.getTransactionLimit() + 100.00);

        assertThrows(IllegalArgumentException.class, () -> transactionService.validateTransaction(transaction));
    }

    @Test
    public void performDeposit_ValidDto_ReturnsDeposit() {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO(bankAccount.getIban(), accounts.get(0).getIban(), 40.00, "deposit");
        Account toAccount = accounts.get(0);
        User user = users.get(0);

        when(accountService.getAccountByIban(requestDTO.fromIban())).thenReturn(bankAccount);
        when(accountService.getAccountByIban(requestDTO.toIban())).thenReturn(toAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transactions.get(3));

        Transaction result = transactionService.performTransaction(requestDTO);

        assertNotNull(result);
        assertEquals(transactions.get(3), result);
        assertEquals(bankAccount, result.getFromAccount());
        assertEquals(toAccount, result.getToAccount());
        assertEquals(user, result.getUser());
        assertEquals(TransactionType.DEPOSIT, result.getTransactionType());
    }

    @Test
    public void performWithdrawal_ValidDto_ReturnsWithdrawal() {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO(accounts.get(0).getIban(), bankAccount.getIban(), 40.00, "withdrawal");
        Account fromAccount = accounts.get(0);
        User user = users.get(0);

        when(accountService.getAccountByIban(requestDTO.fromIban())).thenReturn(fromAccount);
        when(accountService.getAccountByIban(requestDTO.toIban())).thenReturn(bankAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transactions.get(4));

        Transaction result = transactionService.performTransaction(requestDTO);

        assertNotNull(result);
        assertEquals(transactions.get(4), result);
        assertEquals(fromAccount, result.getFromAccount());
        assertEquals(bankAccount, result.getToAccount());
        assertEquals(user, result.getUser());
        assertEquals(TransactionType.WITHDRAWAL, result.getTransactionType());
    }

    @Test
    void validateATM_ValidDeposit_NoExceptionsThrown() {
        Transaction deposit = transactions.get(3);

        assertDoesNotThrow(() -> transactionService.validateATM(deposit));
    }

    @Test
    void validateATM_ValidWithdrawal_NoExceptionsThrown() {
        Transaction withdrawal = transactions.get(4);

        assertDoesNotThrow(() -> transactionService.validateATM(withdrawal));
    }

    @Test
    void validateATM_DepositNullAccount_ThrowsIllegalArgumentException() {
        Transaction deposit = transactions.get(3);
        deposit.setToAccount(null);

        assertThrows(IllegalArgumentException.class, () -> transactionService.validateATM(deposit));
    }

    @Test
    void getUserTransactionsByDay_ReturnsCorrectTransactions() {
        Long userId = users.get(0).getId();
        LocalDate day = LocalDate.now();
        LocalDateTime startTime = day.atStartOfDay();
        LocalDateTime endTime = LocalDateTime.of(day, LocalTime.MAX);

        when(transactionRepository.findTransactionsByUserIdAndTimestampBetween(userId, startTime, endTime))
                .thenReturn(transactions);

        List<Transaction> actualTransactions = transactionService.getUserTransactionsByDay(userId, day);

        assertEquals(transactions, actualTransactions);
        verify(transactionRepository).findTransactionsByUserIdAndTimestampBetween(userId, startTime, endTime);
    }


    @Test
    public void mapTransactionRequestDTOToTransaction_ReturnsMappedTransaction() {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO(accounts.get(0).getIban(), accounts.get(2).getIban(), 10.00, "current to current");

        Transaction result = transactionService.mapTransactionRequestDTOToTransaction(requestDTO);

        assertNotNull(result);
        assertEquals(requestDTO.amount(), result.getAmount());
        assertEquals(requestDTO.description(), result.getDescription());
        assertTrue(Duration.between(LocalDateTime.now(), result.getTimestamp()).toSeconds() < 1);
    }

}
