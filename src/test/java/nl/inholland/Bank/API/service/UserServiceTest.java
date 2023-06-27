package nl.inholland.Bank.API.service;

import io.cucumber.java.bs.A;
import nl.inholland.Bank.API.controller.UserController;
import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.Role;
import nl.inholland.Bank.API.model.Transaction;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.*;
import nl.inholland.Bank.API.repository.AccountRepository;
import nl.inholland.Bank.API.repository.UserRepository;
import nl.inholland.Bank.API.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionService transactionService;
    @Mock
    private AccountService accountService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, transactionService, accountService, bCryptPasswordEncoder, jwtTokenProvider);
    }

    @Test
    void testAdd_ValidUser() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
        user.setBsn("123456789");
        user.setPhoneNumber("+1234567890");

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.add(user);

        assertNotNull(result);
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
    }
    @Test
    void testAdd_InvalidUser() {
        User user = null;

        assertThrows(ResponseStatusException.class, () -> userService.add(user));
    }
    @Test
    void testRegisterChecking_ValidUserRequest() {
        UserRequestDTO userRequest = new UserRequestDTO("John", "Doe", "john.doe@example.com", "StrongPassword123!", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");

        String result = userService.registerChecking(userRequest);

        assertEquals("", result);
    }

//    @Test
//    void testRegisterChecking_ExistingEmail() {
//        UserRequestDTO userRequest = new UserRequestDTO("John", "Doe", "john.doe@example.com", "StrongPassword123!", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");
//        UserRequestDTO userRequest2 = new UserRequestDTO("John", "Doe", "john.doe@example.com", "StrongPassword123!", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");
//
//        userService.registerLogic(userRequest2);
//
//        String result = userService.registerChecking(userRequest);
//
//        assertEquals("User with this email already exists", result);
//    }

    @Test
    void testRegisterChecking_InvalidUserInformation() {
        UserRequestDTO userRequest = new UserRequestDTO("John", "Doe", "john.doe@example.com", "StrongPassword123!", "+4912345678", "123", "2000-10-10", "Mainstreet", 0, "1044CD", "Haarlem", "Netherlands");

        String result = userService.registerChecking(userRequest);

        assertEquals("Bad request. Invalid User Information.", result);
    }

    @Test
    void testRegisterChecking_WeakPassword() {
        UserRequestDTO userRequest = new UserRequestDTO("John", "Doe", "john.doe@example.com", "weak", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");

        String result = userService.registerChecking(userRequest);

        assertEquals("Bad request. Password is not strong enough", result);
    }
    @Test
    void testRegisterLogic_SuccessfulRegistration() {
        UserRequestDTO userRequest = new UserRequestDTO("John", "Doe", "john.doe@example.com", "StrongPassword123!", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");
        User newUser = new User();
        newUser.setFirstName(userRequest.firstName());
        newUser.setLastName(userRequest.lastName());
        newUser.setEmail(userRequest.email());
        newUser.setPassword(userRequest.password());
        newUser.setBsn(userRequest.bsn());
        newUser.setPhoneNumber(userRequest.phoneNumber());
        newUser.setBirthdate(userRequest.birthdate());
        newUser.setStreetName(userRequest.streetName());
        newUser.setHouseNumber(userRequest.houseNumber());
        newUser.setCity(userRequest.city());
        newUser.setZipCode(userRequest.zipCode());
        newUser.setCountry(userRequest.country());
        newUser.setRole(Role.ROLE_USER);
        newUser.setTransactionLimit(100);
        newUser.setDailyLimit(200);

        when(userService.add(newUser)).thenReturn(newUser);

        boolean result = userService.registerLogic(userRequest);
        assertTrue(result);
    }

    @Test
    void testRegisterLogic_FailedRegistration() {
        UserRequestDTO userRequest = new UserRequestDTO("John", "Doe", "john.doe@example.com", "StrongPassword123!", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");
        User newUser = new User();

        when(userService.add(newUser)).thenReturn(null);

        boolean result = userService.registerLogic(userRequest);
        assertFalse(result);
    }
    @Test
    void testUpdate_ValidUserData_Success() {
        Long userId = 1L;
        UserRequestDTO newUserData = new UserRequestDTO("John", "Doe", "john.doe@example.com", "+4912345678", "12345678", "12345678","Haarlem", "Mainstreet", 4, "1044CD", "Netherlands", "2000-10-10");

        User currentUser = new User();
        currentUser.setId(userId);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        Mockito.when(userRepository.save(currentUser)).thenReturn(currentUser);

        boolean result = userService.update(newUserData, userId);

        assertTrue(result);
        assertEquals(newUserData.firstName(), currentUser.getFirstName());
        assertEquals(newUserData.lastName(), currentUser.getLastName());
        assertEquals(newUserData.email(), currentUser.getEmail());
        assertEquals(newUserData.phoneNumber(), currentUser.getPhoneNumber());
        assertEquals(newUserData.bsn(), currentUser.getBsn());
        assertEquals(newUserData.city(), currentUser.getCity());
        assertEquals(newUserData.streetName(), currentUser.getStreetName());
        assertEquals(newUserData.houseNumber(), currentUser.getHouseNumber());
        assertEquals(newUserData.zipCode(), currentUser.getZipCode());
        assertEquals(newUserData.country(), currentUser.getCountry());
        assertEquals(newUserData.birthdate(), currentUser.getBirthdate());

        verify(userRepository, times(1)).save(currentUser);
    }
    @Test
    void testUpdateChecking_ValidUserData_Success() {
        UserRequestDTO newUserData = new UserRequestDTO("John", "Doe", "john.doe@example.com", "+4912345678", "12345678", "12345678","Haarlem", "Mainstreet", 4, "1044CD", "Netherlands", "2000-10-10");
        String userEmail = "john.doe@example.com";

        String result = userService.updateChecking(newUserData, userEmail);

        assertEquals("", result);
    }
    @Test
    void testUpdateChecking_InvalidUserData_BadRequest() {
        UserRequestDTO newUserData = new UserRequestDTO("", "", "john.doe@example.com", "+4912345678", "12345678", "12345678","Haarlem", "Mainstreet", 0, "1044CD", "Netherlands", "2000-10-10");
        String userEmail = "john.doe@example.com";

        String result = userService.updateChecking(newUserData, userEmail);

        assertEquals("Invalid User Information.", result);
    }
    @Test
    public void testGetAllUsers_WithoutAccount_Success() {
        boolean hasAccount = false;
        int skip = 0;
        int limit = 10;

        User user1 = new User();
        User user2 = new User();
        Account account = new Account();
        account.setAccountHolder(user1);
        when(accountRepository.save(account)).thenReturn(account);
        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenReturn(user2);
        when(userRepository.findUsersWithoutAccount()).thenReturn(Arrays.asList(
                user2
        ));

        List<UserResponseDTO> result = userService.getAllUsers(hasAccount, skip, limit);

        assertEquals(1, result.size());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllUsers_Success() {
        boolean hasAccount = false;
        int skip = 0;
        int limit = 10;

        when(userRepository.findUsersWithoutAccount()).thenReturn(Arrays.asList(
                new User(),
                new User()
        ));

        List<UserResponseDTO> result = userService.getAllUsers(hasAccount, skip, limit);

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findUsersWithoutAccount();
    }

    @Test
    public void testGetAllUsers_Pagination_Success() {
        boolean hasAccount = true;
        int skip = 1;
        int limit = 2;

        when(userRepository.findAll()).thenReturn(Arrays.asList(
                new User(),
                new User(),
                new User(),
                new User()
        ));

        List<UserResponseDTO> result = userService.getAllUsers(hasAccount, skip, limit);

        assertEquals(2, result.size());

        verify(userRepository, times(1)).findAll();
    }
    @Test
    public void testGetUserById_ValidId_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUserById_InvalidId_NotFound() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Optional<User> result = userService.getUserById(userId);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(userId);
    }
    @Test
    public void testGetDailyLimit_ValidId_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        int dailyLimit = 100;
        user.setDailyLimit(dailyLimit);

        LocalDateTime today = LocalDateTime.now();
        LocalDate day = LocalDate.now();
        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setUser(user);
        transaction1.setAmount(50.00);
        transaction1.setTimestamp(today);
        Transaction transaction2 = new Transaction();
        transaction2.setId(1L);
        transaction2.setUser(user);
        transaction2.setAmount(30.00);
        transaction2.setTimestamp(today);
        transactions.add(transaction1);
        transactions.add(transaction2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(transactionService.getUserTransactionsByDay(userId, day)).thenReturn(transactions);

        UserDLimitDTO result = userService.getDailyLimit(userId);

        int expectedDailyLimitLeft = dailyLimit - (50 + 30);

        assertEquals(userId, result.userId());
        assertEquals(expectedDailyLimitLeft, result.dailyLimit());
        verify(userRepository, times(1)).findById(userId);
        verify(transactionService, times(1)).getUserTransactionsByDay(userId, day);
    }
    @Test
    public void testGetTransactionLimit_ValidId_Success() {
        // Set up test data
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        int transactionLimit = 200;
        user.setTransactionLimit(transactionLimit);

        // Mock the repository method
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Call the service method
        UserTLimitDTO result = userService.getTransactionLimit(userId);

        // Verify the result
        assertEquals(userId, result.UserId());
        assertEquals(transactionLimit, result.transactionLimit());

        // Verify the repository method is called
        verify(userRepository, times(1)).findById(userId);
    }
    @Test
    public void testUpdateDailyLimit_ValidId_Success() {
        // Set up test data
        Long userId = 1L;
        int newDailyLimit = 200;

        User user = new User();
        user.setId(userId);
        user.setDailyLimit(100);

        // Mock the repository method to return the user
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock the repository save method to return the updated user
        when(userRepository.save(user)).thenReturn(user);

        // Call the service method
        UserDLimitDTO result = userService.updateDailyLimit(userId, newDailyLimit);

        // Verify the result
        assertEquals(userId, result.userId());
        assertEquals(newDailyLimit, result.dailyLimit());

        // Verify the repository method is called
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }
    @Test
    public void testUpdateDailyLimit_InvalidId_Failure() {
        // Set up test data
        Long userId = 2L;
        int newDailyLimit = 200;

        // Mock the repository method to return an empty optional
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Call the service method
        assertThrows(NoSuchElementException.class, () -> userService.updateDailyLimit(userId, newDailyLimit));

        // Verify the repository method is called
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }
    public UserTLimitDTO updateTransactionLimit(Long id, int transactionLimit) {
        Optional<User> response = userRepository.findById(id);
        if (response.isEmpty()) {
            throw new NoSuchElementException("User not found with ID: " + id);
        }

        User user = response.get();
        user.setTransactionLimit(transactionLimit);
        User updatedUser = userRepository.save(user);

        return new UserTLimitDTO(updatedUser.getId(), updatedUser.getTransactionLimit());
    }




}