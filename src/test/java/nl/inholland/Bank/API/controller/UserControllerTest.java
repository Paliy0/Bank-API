package nl.inholland.Bank.API.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.inholland.Bank.API.filter.JwtTokenFilter;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.UserDLimitDTO;
import nl.inholland.Bank.API.model.dto.UserRequestDTO;
import nl.inholland.Bank.API.model.dto.UserResponseDTO;
import nl.inholland.Bank.API.model.dto.UserTLimitDTO;
import nl.inholland.Bank.API.util.JwtTokenProvider;
import nl.inholland.Bank.API.exception.BankAPIExceptionHandler;
import nl.inholland.Bank.API.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;

    private UserController userController;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    static final String EMPLOYEE_TOKEN = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJlbXBsb3llZUBpbmhvbGxhbmQuY29tIiwiYXV0aCI6WyJST0xFX0VNUExPWUVFIl0sImlhdCI6MTY4Nzg4NDcyMCwiZXhwIjoxNjg3OTcxMTIwfQ.fBVLu4JDv6kWawcEWXV3Bo9zjCtrE5x4-M9_Luk_Tx6LIYSGR5cwAe3XC9ZgihjEr4ciUuVN6MSZzH6dAS0nVArVtUI5bRMGdkpz3j95wEPtys1CUi2rlCujoWwuCptxQCgJ7nJk4tn_lroxXnG2sfo_cP-7Cp3HLDCCCZ4KJLP7S9JGoxynncbYHp6r52hfSbQ-SoB3uU8VBbno8LupY3cDX56hNC-EbvBYcrL99pfPy26Tu68Ts1-WMyBpK744_Cphx8SgbHDJdHP6LBqFWE9bkNnm9X9nFGUJcW15K2HJ5J91LNzc5drsLKON7uA4u59MWTOTvC_upiC2Z-e5xQ";
    static final String CUSTOMER_TOKEN = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJjdXN0b21lckBpbmhvbGxhbmQuY29tIiwiYXV0aCI6WyJST0xFX0NVU1RPTUVSIl0sImlhdCI6MTY4Nzg4ODU3OSwiZXhwIjoxNjg3OTc0OTc5fQ.ZLhBMQMs2RQuyHBRo-YGrDZFNoSUjIXOjXocsaZjw2Rp3UVVPiEv6y30LpLmvnnSOC8AJhxsLih4y1rYCN7595L88qml3n4Okk1q99-cRIWHDetaWi4IuScflSo-ewIsBPrTKosQFAg-SEp_AlCWLXcdSOJFdd_VB3Y5ZTNVMex4jMu45knHhC7zL0uW5NsjBgsHRcB31EcXV8y53vvHhzft8xL1rMbXPg_7sjJu3NSAXWdq-RgK0XYWAspKwHYvI78GUktGt8OA-GrxlxQ2_cXQ42RitN3A9u_HQdyLFTmYSr6DgboNduk004swGv1-veG7EBbkW-byY4JC0DqLVg";
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setControllerAdvice(new BankAPIExceptionHandler()) // Add any exception handler if required
                .build();
    }

    @Test
    void testRegisterUser_ValidRequest_ReturnsOK() throws Exception {
        UserRequestDTO userRequest = new UserRequestDTO("Test", "User", "test@user.com", "Test123!", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");

        when(userService.registerChecking(userRequest)).thenReturn(""); // No error
        when(userService.registerLogic(userRequest)).thenReturn(true); // Registration success

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals("User created successfully", mvcResult.getResponse().getContentAsString());
    }


    @Test
    void testRegisterUser_ExceptionThrown_ReturnsBadRequest() throws Exception {
        UserRequestDTO userRequest = new UserRequestDTO("Test", "User", "test@user.com", "Test123!", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");

        when(userService.registerChecking(userRequest)).thenThrow(new RuntimeException("Something went wrong")); // Exception

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        Map<String, String> responseMap = new ObjectMapper().readValue(responseBody, new TypeReference<>() {});

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        assertEquals("Something went wrong", responseMap.get("Error"));
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllUsers_ReturnsListOfUsers() throws Exception {
        List<UserResponseDTO> expectedUsers = List.of(
                new UserResponseDTO(1L, "John", "Doe", null, null, null, null, null, 0, null, null, null, 0, 0, null),
                new UserResponseDTO(2L, "Jane", "Smith", null, null, null, null, null, 0, null, null, null, 0, 0, null)
        );
        Mockito.when(userService.getAllUsers(true, 0, 50)).thenReturn(expectedUsers);

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("skip", "0")
                        .param("limit", "50")
                        .param("hasAccount", "true")
                        .header("Authorization", EMPLOYEE_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value("Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("Jane"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].lastName").value("Smith"));
    }
    @Test
    void testGetAllUsers_InvalidParameters_ReturnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("skip", "-1")
                        .param("limit", "0")
                        .param("hasAccount", "true")
                        .header("Authorization", EMPLOYEE_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    @Test
    void testGetAllUsers_WithoutAccount_Success() throws Exception {
        List<UserResponseDTO> users = List.of(
                new UserResponseDTO(1L, "John", "Doe", null, null, null, null, null, 0, null, null, null, 0, 0, null),
                new UserResponseDTO(2L, "Jane", "Smith", null, null, null, null, null, 0, null, null, null, 0, 0, null)
        );

        Mockito.when(userService.getAllUsers(false, 0, 50)).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("hasAccount", "false")
                        .header("Authorization", EMPLOYEE_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2))); // Expecting 2 users
    }
    @Test
    void testChangeUserData_Success() throws Exception {
        long userId = 5L;
        UserRequestDTO newUserData = new UserRequestDTO("Test", "User", "jane.smith@example.com", "", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");

        User user = new User();
        user.setId(userId);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setEmail("jane.smith@example.com");

        Mockito.when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userService.updateChecking(newUserData, user.getEmail())).thenReturn(""); // Empty error message
        Mockito.when(userService.update(newUserData, userId)).thenReturn(true); // User data updated successfully

        mockMvc.perform(MockMvcRequestBuilders.put("/users/updateInformation/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newUserData))
                        .with(csrf())
                        .header("Authorization", "Bearer " + CUSTOMER_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Information updated successfully"));
    }
    @Test
    void testChangeUserData_EmailAlreadyExists_BadRequest() throws Exception {
        long userId = 5L;
        UserRequestDTO userRequest = new UserRequestDTO("Test", "User", "test@user.com", "Test123!", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");
        UserRequestDTO newUserData = new UserRequestDTO("Test", "User", "test3@user.com", "", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");

        User user = new User();
        user.setId(userId);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setEmail("test@user.com");

        Mockito.when(userService.registerLogic(userRequest)).thenReturn(true);
        Mockito.when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userService.updateChecking(newUserData, user.getEmail())).thenReturn("Email already exists"); // Return error message for existing email

        mockMvc.perform(MockMvcRequestBuilders.put("/users/updateInformation/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newUserData))
                        .with(csrf())
                        .header("Authorization", "Bearer " + CUSTOMER_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Bad request. Email already exists"));
    }
    @Test
    void testGetLoggedInUser_ValidId_Success() throws Exception {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        Mockito.when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId)
                        .header("Authorization", "Bearer " + CUSTOMER_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testGetLoggedInUser_NonExistingUser_ReturnsNotFound() throws Exception {
        long userId = 1L;

        Mockito.when(userService.getUserById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId)
                        .header("Authorization", "Bearer " + CUSTOMER_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("null"));
    }
    @Test
    void testDeleteUser_ValidId_Success() throws Exception {
        long userId = 1L;

        Mockito.when(userService.deleteUserOrDeactivate(userId))
                .thenReturn(ResponseEntity.ok().body("User deleted successfully"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId)
                .header("Authorization", EMPLOYEE_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User deleted successfully"));
    }
    @Test
    void testDeleteUser_InvalidId_ReturnsBadRequest() throws Exception {
        long userId = 5L;

        Mockito.when(userService.deleteUserOrDeactivate(userId))
                .thenReturn(ResponseEntity.badRequest().body("Failed to delete user"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId)
                .header("Authorization", EMPLOYEE_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Failed to delete user"));
    }
    @Test
    public void testGetDailyLimitById_Success() throws Exception {
        long userId = 1L;
        int dailyLimit = 100;

        UserDLimitDTO userDLimitDTO = new UserDLimitDTO(userId, dailyLimit);
        when(userService.getDailyLimit(userId)).thenReturn(userDLimitDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/dailyLimit/{id}", userId)
                        .header("Authorization", "Bearer " + CUSTOMER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"userId\": 1, \"dailyLimit\": 100}"));
    }
    @Test
    public void testUpdateDailyLimitById_Success() throws Exception {
        long userId = 1L;
        int newDailyLimit = 200;

        UserDLimitDTO updatedUserDLimitDTO = new UserDLimitDTO(userId, newDailyLimit);
        when(userService.updateDailyLimit(userId, newDailyLimit)).thenReturn(updatedUserDLimitDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}/dailyLimit", userId)
                        .param("dailyLimit", String.valueOf(newDailyLimit))
                        .header("Authorization", "Bearer " + EMPLOYEE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dailyLimit").value(newDailyLimit));
    }
    @Test
    public void testGetTransactionLimitById_Success() throws Exception {
        long userId = 1L;
        int transactionLimit = 100;

        UserTLimitDTO userTLimitDTO = new UserTLimitDTO(userId, transactionLimit);
        when(userService.getTransactionLimit(userId)).thenReturn(userTLimitDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/transactionLimit/{id}", userId)
                        .header("Authorization", "Bearer " + CUSTOMER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.UserId").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionLimit").value(transactionLimit));
    }

    @Test
    public void testUpdateTransactionLimitById_Success() throws Exception {
        long userId = 1L;
        int newTransactionLimit = 200;

        UserTLimitDTO updatedUserTLimitDTO = new UserTLimitDTO(userId, newTransactionLimit);
        when(userService.updateTransactionLimit(userId, newTransactionLimit)).thenReturn(updatedUserTLimitDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}/transactionLimit", userId)
                        .param("transactionLimit", String.valueOf(newTransactionLimit))
                        .header("Authorization", "Bearer " + EMPLOYEE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.UserId").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionLimit").value(newTransactionLimit));
    }
}




