package nl.inholland.Bank.API.steps.user;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.UserRequestDTO;
import nl.inholland.Bank.API.model.dto.UserResponseDTO;
import nl.inholland.Bank.API.service.UserService;
import org.apache.catalina.connector.Response;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import io.cucumber.java.en.But;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.service.UserService;
import org.apache.catalina.connector.Response;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserSteps {

//    private UserService userService;
//    private User registeredUser;
//    private UserRequestDTO userDetails;
//    private List<UserResponseDTO> allUsers;
//    private int updatedDailyLimit;
//    private int updatedTransactionLimit;
//    private Response serverResponse;
//
//    @Given("the user has logged in as a normal user")
//    public void userLoggedInAsNormalUser() {
//        // Implement the necessary steps to log in as a normal user
//    }
//
//    @When("the client requests to get all users")
//    public void clientRequestsGetAllUsers() {
//        allUsers = userService.getAllUsers(true, 0,50);
//    }
//
//    @Then("the server should respond with status code 200")
//    public void serverRespondsWithStatusCode200() {
//        assertEquals(200, serverResponse.getStatus());
//    }
//
//    @And("the response body should contain a list of users")
//    public void responseBodyContainsListOfUsers() {
//        assertTrue(allUsers.size() > 0);
//    }
//
//    @When("the user provides valid user details")
//    public void userProvidesValidUserDetails() {
//        userDetails = new UserRequestDTO("Test", "User", "test@user.com", "Test123!", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");
//    }
//
//    @When("the client requests to register the user")
//    public void clientRequestsRegisterUser() {
//        Boolean registered = userService.registerLogic(userDetails);
//    }
//
//    @Then("the response body should contain a success message")
//    public void responseBodyContainsSuccessMessage() {
//        String responseBody = serverResponse.getMessage();
//        assertTrue(responseBody.contains("success"));
//    }
//
//    @When("the client requests to delete a user by ID")
//    public void clientRequestsDeleteUserByID() {
//        userService.deleteUserOrDeactivate(userId);
//    }
//
//    @When("the client requests to update the user's daily limit")
//    public void clientRequestsUpdateUserDailyLimit() {
//        updatedDailyLimit = userService.updateUserDailyLimit(userId, newDailyLimit);
//    }
//
//    @Then("the response body should contain the updated daily limit")
//    public void responseBodyContainsUpdatedDailyLimit() {
//        int responseBodyLimit = Integer.parseInt(serverResponse.getBody());
//        assertEquals(updatedDailyLimit, responseBodyLimit);
//    }
//
//    @When("the client requests to update the user's transaction limit")
//    public void clientRequestsUpdateUserTransactionLimit() {
//        updatedTransactionLimit = userService.updateUserTransactionLimit(userId, newTransactionLimit);
//    }
//
//    @Then("the response body should contain the updated transaction limit")
//    public void responseBodyContainsUpdatedTransactionLimit() {
//        int responseBodyLimit = Integer.parseInt(serverResponse.getBody());
//        assertEquals(updatedTransactionLimit, responseBodyLimit);
//    }
//


}
