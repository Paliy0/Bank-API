package nl.inholland.Bank.API.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.model.AccountType;
import nl.inholland.Bank.API.model.Role;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.AccountRequestDTO;
import nl.inholland.Bank.API.model.dto.AccountResponseDTO;
import nl.inholland.Bank.API.model.dto.AccountUserResponseDTO;
import nl.inholland.Bank.API.model.dto.FindAccountResponseDTO;
import nl.inholland.Bank.API.model.dto.MyAccountResponseDTO;
import nl.inholland.Bank.API.model.dto.StatusAccountRequestDTO;
import nl.inholland.Bank.API.service.AccountService;
import nl.inholland.Bank.API.service.UserService;
import nl.inholland.Bank.API.util.JwtTokenProvider;

@EnableMethodSecurity
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;


    private AccountResponseDTO responseDTO;

    private User employee;
    
    private User customer;

    private AccountUserResponseDTO accountHolder;

    @MockBean
    private UserService userService;

    @BeforeEach
    void init() {
        customer = new User();
        customer.setFirstName("Sasa");
        customer.setLastName("Crow");
        customer.setPassword("Test123!");
        customer.setEmail("customer@inholland.com");
        customer.setBsn("350876412");
        customer.setPhoneNumber("+31669519063");
        customer.setBirthdate("1999-05-14");
        customer.setStreetName("Schoonzichtlaan");
        customer.setHouseNumber(218);
        customer.setZipCode("2015 CL");
        customer.setCity("Haarlem");
        customer.setCountry("Netherlands");
        customer.setDailyLimit(1000);
        customer.setTransactionLimit(100);
        customer.setRole(Role.ROLE_CUSTOMER);
        
        employee = new User();
        employee.setFirstName("John");
        employee.setLastName("Weak");
        employee.setPassword("Test123!");
        employee.setEmail("employee@inholland.com");
        employee.setBsn("456744494");
        employee.setPhoneNumber("+31667628938");
        employee.setBirthdate("1990-07-20");
        employee.setStreetName("Nieuwe Hoogstraat");
        employee.setHouseNumber(14);
        employee.setZipCode("1011 HC");
        employee.setCity("Amsterdam");
        employee.setCountry("Netherlands");
        employee.setDailyLimit(10000);
        employee.setTransactionLimit(1000);
        employee.setRole(Role.ROLE_EMPLOYEE);

        Account userCurrentAccount = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, customer);
        userCurrentAccount.setIban(accountService.generateIBAN());
        userCurrentAccount.setBalance(250.25);

        Account userSavingsAccount = new Account(AccountType.SAVINGS, AccountStatus.ACTIVE, customer);
        userSavingsAccount.setIban(accountService.generateIBAN());
        userSavingsAccount.setBalance(249.75);

        Account employeeCurrent = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, employee);
        employeeCurrent.setIban(accountService.generateIBAN());
        employeeCurrent.setBalance(2411.51);
        accountHolder = new AccountUserResponseDTO(1L, "John", "Doe", "", "", "", "", "");
        responseDTO = new AccountResponseDTO(1L, "NL010236546", 0.0, 0.0, LocalDate.now(), AccountType.CURRENT, AccountStatus.ACTIVE, accountHolder);
    }
    @Test
    @WithMockUser(username = "employee@inholland.com", roles = {"EMPLOYEE"})
    void getAllAccounts() throws Exception {

        when(accountService.getAllAccounts(1,0)).thenReturn(List.of(new AccountResponseDTO(1L, "NL010236546", 0.0, 0.0, LocalDate.now(), AccountType.CURRENT, AccountStatus.ACTIVE, accountHolder)));
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/accounts")
                    .param("limit", "1")
                    .param("offset", "0"))
            .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(responseDTO.getId()))
            .andExpect(jsonPath("$[0].iban").value(responseDTO.getIban()))
            .andExpect(jsonPath("$[0].balance").value(responseDTO.getBalance()))
            .andExpect(jsonPath("$[0].absoluteLimit").value(responseDTO.getAbsoluteLimit()))
            .andExpect(jsonPath("$[0].createdAt").value(responseDTO.getCreatedAt().toString()))
            .andExpect(jsonPath("$[0].accountType").value(responseDTO.getAccountType().toString()))
            .andExpect(jsonPath("$[0].accountStatus").value(responseDTO.getAccountStatus().toString()))
            .andExpect(jsonPath("$[0].user").isNotEmpty()); 
    }

    @Test
    @WithAnonymousUser
    void getAllAccountsWithAnonymousUserShouldReturnUnauthorised() throws Exception {

        when(accountService.getAllAccounts(1,0)).thenReturn(List.of(new AccountResponseDTO()));
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/accounts")
                    .param("limit", "1")
                    .param("offset", "0"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "customer@inholland.com", roles = {"CUSTOMER"})
    void getAllAccountsWithCustomerShouldReturnUnauthorised() throws Exception {

        when(accountService.getAllAccounts(1,0)).thenReturn(List.of(new AccountResponseDTO()));
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/accounts")
                    .param("limit", "1")
                    .param("offset", "0"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "customer@inholland.com", roles = {"USER"})
    void getAllAccountsWithUserShouldReturnUnauthorised() throws Exception {

        when(accountService.getAllAccounts(1,0)).thenReturn(List.of(new AccountResponseDTO()));
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/accounts")
                    .param("limit", "1")
                    .param("offset", "0"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "employee@inholland.com", roles = {"EMPLOYEE"})
    void getIbanByCustomerName() throws Exception {

        String firstName = "Sasa";
        FindAccountResponseDTO response = new FindAccountResponseDTO("NL0123456789", firstName, "CURRENT");
        List<FindAccountResponseDTO> accountList = List.of(response);

        when(accountService.getIbanByCustomerName(firstName)).thenReturn(accountList);

        this.mockMvc.perform(
            MockMvcRequestBuilders.get("/accounts/getIbanByCustomerName")
                    .param("firstName", firstName))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].iban").value(response.getIban()))
            .andExpect(jsonPath("$[0].user").value(response.getUser()))
            .andExpect(jsonPath("$[0].accountType").value(response.getAccountType()));
    }
    @Test
    @WithMockUser(username = "employee@inholland.com", roles = {"EMPLOYEE"})
        void getIbanByCustomerNameIsNotFound() throws Exception {

        String firstName = "Sasa";

        // Return an empty list when no accounts are found.
        when(accountService.getIbanByCustomerName(firstName)).thenReturn(List.of());

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/accounts/getIbanByCustomerName")
                        .param("firstName", firstName))
                .andDo(print())
                // The status is changed to isNotFound to match the method name.
                .andExpect(status().isNotFound());
        }

    @Test
    @WithMockUser(username = "employee@inholland.com", roles = {"EMPLOYEE", "CUSTOMER"})
    void getMyAccounts() throws Exception {

        Long userId = 1L;
        MyAccountResponseDTO response = new MyAccountResponseDTO(
            "NL0123456789",
            100.0,
            500.0,
            LocalDate.now(),
            AccountType.CURRENT,
            AccountStatus.ACTIVE,
            100.0
        );
        List<MyAccountResponseDTO> accountList = List.of(response);

        when(accountService.findAccountsByLoggedInUser(userId)).thenReturn(accountList);

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/accounts/myAccounts/{userId}", userId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].iban").value(response.getIban()))
            .andExpect(jsonPath("$[0].balance").value(response.getBalance()))
            .andExpect(jsonPath("$[0].absoluteLimit").value(response.getAbsoluteLimit()))
            .andExpect(jsonPath("$[0].createdAt").value(response.getCreatedAt().toString())) // assuming it's formatted as yyyy-MM-dd
            .andExpect(jsonPath("$[0].accountType").value(response.getAccountType().toString()))
            .andExpect(jsonPath("$[0].accountStatus").value(response.getAccountStatus().toString()))
            .andExpect(jsonPath("$[0].totalBalance").value(response.getTotalBalance()));
    }

    @Test
    @WithMockUser(username = "employee@inholland.com", roles = {"EMPLOYEE", "CUSTOMER"})
    void getMyAccounts_userNotFound() throws Exception {

        Long userId = 1L; 

        when(accountService.findAccountsByLoggedInUser(userId)).thenReturn(Collections.emptyList());

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/myAccounts/{userId}", userId))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "employee@inholland.com", roles = {"EMPLOYEE", "CUSTOMER"})
    void getMyAccounts_multipleAccounts() throws Exception {

        Long userId = 1L; 
        MyAccountResponseDTO response1 = new MyAccountResponseDTO("NL0123456789", 100.0, 500.0, LocalDate.now(), AccountType.CURRENT, AccountStatus.ACTIVE, 100.0);
        MyAccountResponseDTO response2 = new MyAccountResponseDTO("NL9876543210", 200.0, 500.0, LocalDate.now(), AccountType.SAVINGS, AccountStatus.ACTIVE, 200.0);
        
        List<MyAccountResponseDTO> accountList = List.of(response1, response2);

        when(accountService.findAccountsByLoggedInUser(userId)).thenReturn(accountList);

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/accounts/myAccounts/{userId}", userId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].iban").value(response1.getIban()))
            .andExpect(jsonPath("$[0].balance").value(response1.getBalance()))
            .andExpect(jsonPath("$[0].absoluteLimit").value(response1.getAbsoluteLimit()))
            .andExpect(jsonPath("$[0].createdAt").value(response1.getCreatedAt().toString()))
            .andExpect(jsonPath("$[0].accountType").value(response1.getAccountType().toString()))
            .andExpect(jsonPath("$[0].accountStatus").value(response1.getAccountStatus().toString()))
            .andExpect(jsonPath("$[0].totalBalance").value(response1.getTotalBalance()))
            .andExpect(jsonPath("$[1].iban").value(response2.getIban()))
            .andExpect(jsonPath("$[1].balance").value(response2.getBalance()))
            .andExpect(jsonPath("$[1].absoluteLimit").value(response2.getAbsoluteLimit()))
            .andExpect(jsonPath("$[1].createdAt").value(response2.getCreatedAt().toString())) 
            .andExpect(jsonPath("$[1].accountType").value(response2.getAccountType().toString()))
            .andExpect(jsonPath("$[1].accountStatus").value(response2.getAccountStatus().toString()))
            .andExpect(jsonPath("$[1].totalBalance").value(response2.getTotalBalance()));
    }

    @Test
    @WithMockUser(username = "employee@inholland.com", roles = {"EMPLOYEE", "CUSTOMER"})
    void getAccountByIban_success() throws Exception {

        String iban = "NL0123456789";
        AccountResponseDTO response = new AccountResponseDTO(1L, iban, 1000.0, 500.0, LocalDate.now(), AccountType.CURRENT, AccountStatus.ACTIVE, new AccountUserResponseDTO());

        when(accountService.getAccountByIban2(iban)).thenReturn(response);

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/accounts/{iban}", iban))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.getId()))
            .andExpect(jsonPath("$.iban").value(response.getIban()))
            .andExpect(jsonPath("$.balance").value(response.getBalance()))
            .andExpect(jsonPath("$.absoluteLimit").value(response.getAbsoluteLimit()))
            .andExpect(jsonPath("$.accountType").value(response.getAccountType().toString()))
            .andExpect(jsonPath("$.accountStatus").value(response.getAccountStatus().toString()));
    }

    @Test
    @WithMockUser(username = "employee@inholland.com", roles = {"EMPLOYEE", "CUSTOMER"})
    void getAccountByIban_accountNotExist() throws Exception {

        String iban = "NL0123456789";

        when(accountService.getAccountByIban2(iban)).thenThrow(new RuntimeException());

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/accounts/{iban}", iban))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$").value("Error retrieving account: Account with this IBAN doesn't exist"));
    }

        @Test
        @WithMockUser(username = "employee@inholland.com", roles = {"EMPLOYEE"})
        void insertAccount_success() throws Exception {
        AccountRequestDTO request = new AccountRequestDTO(AccountStatus.ACTIVE, AccountType.CURRENT, new AccountUserResponseDTO(1L, "John", "Doe", "f", "f", "d", "f", "f"));

        when(accountService.createAccount(request)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(null));

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/accounts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
        }


    @Test
    @WithMockUser(username = "employee@inholland.com", roles = {"EMPLOYEE"})
    void insertAccount_serverError() throws Exception {

        AccountRequestDTO request = new AccountRequestDTO(AccountStatus.ACTIVE, AccountType.CURRENT, new AccountUserResponseDTO());

        when(accountService.createAccount((any(AccountRequestDTO.class)))).thenThrow(new RuntimeException());

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/accounts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$").value("Unexpected server error"));
    }

    @Test
    @WithMockUser(username = "employee@inholland.com", roles = {"EMPLOYEE"})
    void updateAccountStatus_success() throws Exception {

        String iban = "NL0123456789";
        StatusAccountRequestDTO request = new StatusAccountRequestDTO(AccountStatus.ACTIVE.name());

        AccountResponseDTO responseDTO = new AccountResponseDTO(1L, iban, 1000.0, 500.0, LocalDate.now(), AccountType.CURRENT, AccountStatus.ACTIVE, new AccountUserResponseDTO());

        when(accountService.updateAccountStatus(eq(iban), any(AccountStatus.class))).thenReturn(responseDTO);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/accounts/accountStatus/{iban}", iban)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value("Account status updated successfully: " + responseDTO.toString()));
    }


    @Test
    @WithMockUser(username = "employee@inholland.com", roles = {"EMPLOYEE"})
    void updateAccountStatus_serverError() throws Exception {

        String iban = "NL0123456789";
        StatusAccountRequestDTO request = new StatusAccountRequestDTO(AccountStatus.ACTIVE.name());

        when(accountService.updateAccountStatus(eq(iban), any(AccountStatus.class))).thenThrow(new RuntimeException());

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/accounts/accountStatus/{iban}", iban)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .content(new ObjectMapper().writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$").value("Unexpected server error"));
    }
}