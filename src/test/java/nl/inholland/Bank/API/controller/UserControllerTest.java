package nl.inholland.Bank.API.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.inholland.Bank.API.filter.JwtTokenFilter;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.UserRequestDTO;
import nl.inholland.Bank.API.model.dto.UserResponseDTO;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
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
    static final String CUSTOMER_TOKEN = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJjdXN0b21lckBpbmhvbGxhbmQuY29tIiwiYXV0aCI6WyJST0xFX0NVU1RPTUVSIl0sImlhdCI6MTY4Nzg4ODU3OSwiZXhwIjoxNjg3OTc0OTc5fQ.ZLhBMQMs2RQuyHBRo-YGrDZFNoSUjIXOjXocsaZjw2Rp3UVVPiEv6y30LpLmvnnSOC8AJhxsLih4y1rYCN7595L88qml3n4Okk1q99-cRIWHDetaWi4IuScflSo-ewIsBPrTKosQFAg-SEp_AlCWLXcdSOJFdd_VB3Y5ZTNVMex4jMu45knHhC7zL0uW5NsjBgsHRcB31EcXV8y53vvHhzft8xL1rMbXPg_7sjJu3NSAXWdq-RgK0XYWAspKwHYvI78GUktGt8OA-GrxlxQ2_cXQ42RitN3A9u_HQdyLFTmYSr6DgboNduk004swGv1-veG7EBbkW-byY4JC0DqLVg";
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setControllerAdvice(new BankAPIExceptionHandler()) // Add any exception handler if required
                .build();
    }

    @Test
    void testRegisterUser_ValidRequest_ReturnsOK() throws Exception {
        // Create a sample user request
        UserRequestDTO userRequest = new UserRequestDTO("Test", "User", "test@user.com", "Test123!", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");

        // Mock the UserService methods
        when(userService.registerChecking(userRequest)).thenReturn(""); // No error
        when(userService.registerLogic(userRequest)).thenReturn(true); // Registration success

        // Perform the POST request to the "/users/register" endpoint
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Assert the response status code and body
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals("User created successfully", mvcResult.getResponse().getContentAsString());
    }


    @Test
    void testRegisterUser_ExceptionThrown_ReturnsBadRequest() throws Exception {
        // Create a sample user request
        UserRequestDTO userRequest = new UserRequestDTO("Test", "User", "test@user.com", "Test123!", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");

        // Mock the UserService methods
        when(userService.registerChecking(userRequest)).thenThrow(new RuntimeException("Something went wrong")); // Exception

        // Perform the POST request to the "/users/register" endpoint
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        // Extract the response body and convert it to a map
        String responseBody = mvcResult.getResponse().getContentAsString();
        Map<String, String> responseMap = new ObjectMapper().readValue(responseBody, new TypeReference<>() {});

        // Assert the response status code and body
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        assertEquals("Something went wrong", responseMap.get("Error"));
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllUsers_ReturnsListOfUsers() throws Exception {
        // Mock the userService.getAllUsers method and define the expected list of users
        List<UserResponseDTO> expectedUsers = List.of(
                new UserResponseDTO(1L, "John", "Doe", null, null, null, null, null, 0, null, null, null, 0, 0, null),
                new UserResponseDTO(2L, "Jane", "Smith", null, null, null, null, null, 0, null, null, null, 0, 0, null)
        );
        Mockito.when(userService.getAllUsers(true, 0, 50)).thenReturn(expectedUsers);

        // Perform the GET request to the "/users" endpoint
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
        // Perform the GET request to the "/users" endpoint with invalid parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("skip", "-1")
                        .param("limit", "0")
                        .param("hasAccount", "true")
                        .header("Authorization", EMPLOYEE_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    @Test
    void testGetAllUsers_WithoutAccount_ReturnsUsers() throws Exception {
        // Create a sample list of users with and without an account
        List<UserResponseDTO> users = List.of(
                new UserResponseDTO(1L, "John", "Doe", null, null, null, null, null, 0, null, null, null, 0, 0, null),
                new UserResponseDTO(2L, "Jane", "Smith", null, null, null, null, null, 0, null, null, null, 0, 0, null)
        );


        // Mock the UserService and define the expected list of users
        Mockito.when(userService.getAllUsers(false, 0, 50)).thenReturn(users);

        // Perform the GET request to the "/users" endpoint with hasAccount=true
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("hasAccount", "false")
                        .header("Authorization", EMPLOYEE_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2))); // Expecting 2 users
    }
    @Test
    void testChangeUserData_ValidData_ReturnsSuccessMessage() throws Exception {
        // Prepare test data
        long userId = 1L;
        UserRequestDTO newUserData = new UserRequestDTO("Test", "User", "test@user.com", "", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");

        User user = new User();
        user.setId(userId);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setEmail("jane.smith@example.com");

        Mockito.when(userService.updateChecking(newUserData, user.getEmail())).thenReturn(""); // Empty error message
        Mockito.when(userService.update(newUserData, userId)).thenReturn(true); // User data updated successfully

        // Perform the PUT request to the "/updateInformation/{id}" endpoint
        mockMvc.perform(MockMvcRequestBuilders.put("/updateInformation/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newUserData))
                        .with(csrf())
                        .header("Authorization", "Bearer " + CUSTOMER_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Information updated successfully"));
    }
}




