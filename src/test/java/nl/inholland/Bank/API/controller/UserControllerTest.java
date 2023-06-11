package nl.inholland.Bank.API.controller;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nl.inholland.Bank.API.model.dto.UserResponseDTO;
import nl.inholland.Bank.API.service.UserService;

public class UserControllerTest {
    private UserController userController;
    private ResponseEntity<Iterable<UserResponseDTO>> response;

    @Given("the application is running")
    public void theApplicationIsRunning(UserService userService) {
        // Implement any necessary setup before running the test
        userController = new UserController(userService);
    }

    @When("I request to get all users")
    public void iRequestToGetAllUsers() {
        response = userController.getAllUsers(0, 50, true);
    }

    @Then("the response should be a list of users")
    public void theResponseShouldBeAListOfUsers() {
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Iterable<UserResponseDTO> users = response.getBody();
        Assert.assertNotNull(users);
    }

    @Then("the response should have a status code of {int}")
    public void theResponseShouldHaveAStatusCodeOf(int statusCode) {
        Assert.assertEquals(HttpStatus.valueOf(statusCode), response.getStatusCode());
    }

    @When("I request to get users without an account")
    public void iRequestToGetUsersWithoutAnAccount() {
        response = userController.getAllUsers(0, 50, false);
    }

    @Then("the response should be a list of users without an account")
    public void theResponseShouldBeAListOfUsersWithoutAnAccount() {
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Iterable<UserResponseDTO> users = response.getBody();
        Assert.assertNotNull(users);
        for (UserResponseDTO user : users) {
            //Assert.assertFalse(user.hasAccount());
        }
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void registerUser() {
    }

    @Test
    void changeUserData() {
    }

    @Test
    void getLoggedInUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void getDailyLimitById() {
    }

    @Test
    void updateDailyLimitById() {
    }

    @Test
    void getTransactionLimitById() {
    }

    @Test
    void updateTransactionLimitById() {
    }
}
