package nl.inholland.Bank.API.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.inholland.Bank.API.exception.BankAPIExceptionHandler;
import nl.inholland.Bank.API.filter.JwtTokenFilter;
import nl.inholland.Bank.API.model.*;
import nl.inholland.Bank.API.model.dto.TransactionRequestDTO;
import nl.inholland.Bank.API.model.dto.TransactionResponseDTO;
import nl.inholland.Bank.API.service.AccountService;
import nl.inholland.Bank.API.service.TransactionService;
import nl.inholland.Bank.API.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private AccountService accountService;

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
        accountService = mock(AccountService.class);

        User bankUser = new User(1L, "BANK", "INHOLLAND", "SecretPassword123", "bank@inholland.com", "203514435", "" +
                "+31697389599", "1980-01-01", "Bijdorplaan", 15, "2015 CE", "Haarlem", "Netherlands",
                1000000, 1000000, Role.ROLE_EMPLOYEE);


        bankAccount = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, bankUser);
        bankAccount.setIban("NL01INHO0000000001");
        bankAccount.setBalance(1000000);

        mockMvc = MockMvcBuilders.standaloneSetup(new TransactionController(transactionService))
                .setControllerAdvice(new BankAPIExceptionHandler())
                .build();

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
    void getAllTransactions_ReturnsListOfTransactions() throws Exception {
        ArgumentCaptor<Long> accountIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<LocalDate> fromDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> toDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<Double> minAmountCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> maxAmountCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<TransactionType> transactionTypeCaptor = ArgumentCaptor.forClass(TransactionType.class);
        ArgumentCaptor<String> senderCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> receiverCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> pageCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);

        Page<Transaction> page = new PageImpl<>(transactions);
            Mockito.when(transactionService.getAllTransactions(
                        accountIdCaptor.capture(), fromDateCaptor.capture(), toDateCaptor.capture(),
                        minAmountCaptor.capture(), maxAmountCaptor.capture(), transactionTypeCaptor.capture(),
                        senderCaptor.capture(), receiverCaptor.capture(), pageCaptor.capture(), sizeCaptor.capture()))
                .thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/transactions")
                        .header("Authorization", EMPLOYEE_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(transactions.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1));
    }

    @Test
    void postTransaction_ValidDto_ReturnsCreatedResponse() throws Exception {
        TransactionRequestDTO requestDto = new TransactionRequestDTO(accounts.get(0).getIban(), accounts.get(2).getIban(), 10.00, "current to current");

        when(transactionService.performTransaction(any(TransactionRequestDTO.class))).thenReturn(transactions.get(0));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                        .header("Authorization", EMPLOYEE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, mvcResult.getResponse().getContentType());
    }

    @Test
    void postTransaction_NullDto_ReturnsBadRequestResponse() throws Exception {
        when(transactionService.performTransaction(null)).thenReturn(null); ;

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                        .header("Authorization", EMPLOYEE_TOKEN)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON_VALUE, mvcResult.getResponse().getContentType());
    }

    @Test
    void postDeposit_ValidDto_ReturnsCreatedResponse() throws Exception {
        TransactionRequestDTO requestDto = new TransactionRequestDTO(bankAccount.getIban(), accounts.get(0).getIban(), 40.00, "deposit");

        when(transactionService.performDeposit(any(TransactionRequestDTO.class))).thenReturn(transactions.get(3));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/transactions/atm/deposit")
                        .header("Authorization", EMPLOYEE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, mvcResult.getResponse().getContentType());
    }

    @Test
    void postDeposit_NullDto_ReturnsBadRequestResponse() throws Exception {
        when(transactionService.performDeposit(null)).thenReturn(null); ;

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/transactions/atm/deposit")
                        .header("Authorization", EMPLOYEE_TOKEN)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON_VALUE, mvcResult.getResponse().getContentType());
    }

    @Test
    void postWithdrawal_ValidDto_ReturnsCreatedResponse() throws Exception {
        TransactionRequestDTO requestDto = new TransactionRequestDTO(accounts.get(0).getIban(), bankAccount.getIban(), 40.00, "deposit");

        when(transactionService.performWithdrawal(any(TransactionRequestDTO.class))).thenReturn(transactions.get(4));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/transactions/atm/withdrawal")
                        .header("Authorization", EMPLOYEE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, mvcResult.getResponse().getContentType());
    }

    @Test
    void postWithdrawal_NullDto_ReturnsBadRequestResponse() throws Exception {
        when(transactionService.performWithdrawal(null)).thenReturn(null);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/transactions/atm/withdrawal")
                        .header("Authorization", EMPLOYEE_TOKEN)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON_VALUE, mvcResult.getResponse().getContentType());
    }
}