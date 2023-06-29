package nl.inholland.Bank.API.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.model.AccountType;
import nl.inholland.Bank.API.model.Role;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.AccountResponseDTO;
import nl.inholland.Bank.API.model.dto.AccountUserResponseDTO;
import nl.inholland.Bank.API.model.dto.FindAccountResponseDTO;
import nl.inholland.Bank.API.model.dto.MyAccountResponseDTO;
import nl.inholland.Bank.API.repository.AccountRepository;

@ExtendWith({SpringExtension.class})
public class AccountServiceTest {
    
   @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    private AccountService accountService;

    private ModelMapper modelMapper;

    @BeforeEach
    void init() {
        modelMapper = new ModelMapper();
        accountService = new AccountService(accountRepository, userService); // manually inject the dependencies
    }

    @Test
public void getAllAccounts_success() throws Exception{
    // Given
        User user1 = new User();
        user1.setId(1L);
            user1.setFirstName("Sasa");
            user1.setLastName("Crow");
            user1.setPassword("Test123!");
            user1.setEmail("user1@inholland.com");
            user1.setBsn("350876412");
            user1.setPhoneNumber("+31669519063");
            user1.setBirthdate("1999-05-14");
            user1.setStreetName("Schoonzichtlaan");
            user1.setHouseNumber(218);
            user1.setZipCode("2015 CL");
            user1.setCity("Haarlem");
            user1.setCountry("Netherlands");
            user1.setDailyLimit(1000);
            user1.setTransactionLimit(100);
            user1.setRole(Role.ROLE_CUSTOMER);

            User user2 = new User();
            user2.setId(null);
            user2.setFirstName("Sasa");
            user2.setLastName("Crow");
            user2.setPassword("Test123!");
            user2.setEmail("customer@inholland.com");
            user2.setBsn("350876412");
            user2.setPhoneNumber("+31669519063");
            user2.setBirthdate("1999-05-14");
            user2.setStreetName("Schoonzichtlaan");
            user2.setHouseNumber(218);
            user2.setZipCode("2015 CL");
            user2.setCity("Haarlem");
            user2.setCountry("Netherlands");
            user2.setDailyLimit(1000);
            user2.setTransactionLimit(100);
            user2.setRole(Role.ROLE_CUSTOMER);

        Account account1 = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, user1);
            account1.setIban("NL0123456789");
            account1.setBalance(250.25);

            Account account2 = new Account(AccountType.SAVINGS, AccountStatus.ACTIVE, user2);
            account2.setIban("NL0987654321");
            account2.setBalance(249.75);

        Iterable<Account> accounts = List.of(account1, account2);
        when(accountRepository.findAllByIbanNot("NL01INHO0000000001")).thenReturn(accounts);
        
        when(userService.getUserById(account1.getAccountHolder().getId())).thenReturn(Optional.of(user1));
        when(userService.getUserById(account2.getAccountHolder().getId())).thenReturn(Optional.of(user2));

        AccountResponseDTO responseDTO1 = modelMapper.map(account1, AccountResponseDTO.class); 
        AccountResponseDTO responseDTO2 = modelMapper.map(account2, AccountResponseDTO.class);

        AccountUserResponseDTO userResponseDTO1 = modelMapper.map(user1, AccountUserResponseDTO.class);
        AccountUserResponseDTO userResponseDTO2 = modelMapper.map(user2, AccountUserResponseDTO.class);

        responseDTO1.setUser(userResponseDTO1);
        responseDTO2.setUser(userResponseDTO2);

        // When
        List<AccountResponseDTO> result = accountService.getAllAccounts(2, 0);

        // Then
        assertEquals(2, result.size()); // Checks if the result has the expected size
        assertEquals(responseDTO1, result.get(0)); // Checks if the first item is as expected
        assertEquals(responseDTO2, result.get(1)); // Checks if the second item is as expected
    }   

    @Test
    public void getAllAccounts_noAccountsFound() throws Exception{
        // Given no accounts in the repository
        when(accountRepository.findAllByIbanNot("NL01INHO0000000001")).thenReturn(List.of());

        // When
        List<AccountResponseDTO> result = accountService.getAllAccounts(2, 0);

        // Then
        assertTrue(result.isEmpty()); // The result should be an empty list
    }

    @Test
    public void findAccountsByLoggedInUser_success() {
        // Given
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Account account1 = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, user);
        account1.setIban("NL0123456789");
        account1.setBalance(250.25);

        Account account2 = new Account(AccountType.SAVINGS, AccountStatus.ACTIVE, user);
        account2.setIban("NL0987654321");
        account2.setBalance(249.75);

        Iterable<Account> accounts = List.of(account1, account2);
        when(accountRepository.findAccountsByAccountHolder_Id(userId)).thenReturn(accounts);

        // When
        List<MyAccountResponseDTO> result = accountService.findAccountsByLoggedInUser(userId);

        // Then
        assertEquals(2, result.size());
        assertEquals(account1.getBalance() + account2.getBalance(), result.get(0).getTotalBalance());
        assertEquals(account1.getBalance() + account2.getBalance(), result.get(1).getTotalBalance());
    }

    @Test
    public void findAccountsByLoggedInUser_noAccountsFound() {
        // Given
        Long userId = 1L;

        when(accountRepository.findAccountsByAccountHolder_Id(userId)).thenReturn(new ArrayList<>());

        // When
        List<MyAccountResponseDTO> result = accountService.findAccountsByLoggedInUser(userId);

        // Then
        assertTrue(result.isEmpty());
    }


    @Test
    public void getIbanByCustomerName_success() {
        // Given
        String firstName = "Sasa";

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName("Crow");

        Account account = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, user);
        account.setIban("NL0123456789");

        Iterable<Account> accounts = List.of(account);
        when(accountRepository.findIbanByAccountHolder_FirstName(firstName)).thenReturn(accounts);

        // When
        List<FindAccountResponseDTO> result = accountService.getIbanByCustomerName(firstName);

        // Then
        assertEquals(1, result.size());
        assertEquals(account.getIban(), result.get(0).getIban());
        assertEquals(user.getFirstName() + " " + user.getLastName(), result.get(0).getUser());
    }

    @Test
    public void getIbanByCustomerName_noAccountsFound() {
        // Given
        String firstName = "Nonexistent";

        when(accountRepository.findIbanByAccountHolder_FirstName(firstName)).thenReturn(List.of());

        // When
        List<FindAccountResponseDTO> result = accountService.getIbanByCustomerName(firstName);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    public void getAccountByIban2_success() {
        // Given
        String iban = "NL0123456789";
        User user = new User();
        user.setFirstName("Sasa");
        user.setLastName("Crow");

        Account account = new Account(AccountType.CURRENT, AccountStatus.ACTIVE, user);
        account.setIban(iban);

        when(accountRepository.findAccountByIbanAndIbanNot(iban, "NL01INHO0000000001")).thenReturn(account);
        when(userService.getUserById(account.getAccountHolder().getId())).thenReturn(Optional.of(user));

        // When
        AccountResponseDTO result = accountService.getAccountByIban2(iban);

        // Then
        assertEquals(iban, result.getIban());
        assertEquals(user.getFirstName(), result.getUser().getFirstName());
        assertEquals(user.getLastName(), result.getUser().getLastName());
    }

    @Test
    public void getAccountByIban2_noAccountFound() {
        // Given
        String iban = "NL0123456789";

        when(accountRepository.findAccountByIbanAndIbanNot(iban, "NL01INHO0000000001")).thenThrow(new IllegalArgumentException("Account not found"));

        // When - Then
        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountByIban2(iban));
    }
}