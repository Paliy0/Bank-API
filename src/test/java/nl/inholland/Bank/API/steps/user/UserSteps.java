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
//    @Given("the user has logged in as a normal user")
//    public void userLoggedInAsNormalUser() {
//        // Implement the necessary steps to log in as a normal user
//    }
//
//    @Given("the user provides valid user details")
//    public void userProvidesValidUserDetails() {
//        // Implement the necessary steps for the user to provide valid user details
//    }
//
//    @When("the client requests to register the user")
//    public void clientRequestsToRegisterUser() {
//        // Implement the steps to make a request to register the user
//    }
//
//    @Then("the server should respond with status code {int}")
//    public void serverRespondsWithStatusCode(int statusCode) {
//        // Implement the steps to assert the server's response status code
//    }
//
//    @Given("the user is logged in and provides data to be changed")
//    public void userLoggedInAndProvidesDataToBeChanged() {
//        // Implement the necessary steps for the user to be logged in and provide data to be changed
//    }
//
//    @When("the user requests to change the data")
//    public void userRequestsToChangeData() {
//        // Implement the steps to make a request to change the user's data
//    }
//
//    @Given("the user is logged in and wants to get the transaction limit")
//    public void userLoggedInAndWantsToGetTransactionLimit() {
//        // Implement the necessary steps for the user to be logged in and want to get the transaction limit
//    }
//
//    @When("the client requests to get the transaction limit")
//    public void clientRequestsToGetTransactionLimit() {
//        // Implement the steps to make a request to get the transaction limit
//    }
//
//    @Then("the server should respond with status code 200")
//    public void serverRespondsWithStatusCode200() {
//        // Implement the steps to assert the server's response with status code 200
//    }
//
//    @And("the server responds with the transaction limit")
//    public void serverRespondsWithTransactionLimit() {
//        // Implement the steps to assert the server's response with the transaction limit
//    }
//
//    @Given("the user is logged in and wants to get the daily limit")
//    public void userLoggedInAndWantsToGetDailyLimit() {
//        // Implement the necessary steps for the user to be logged in and want to get the daily limit
//    }
//
//    @When("the client requests to get the daily limit")
//    public void clientRequestsToGetDailyLimit() {
//        // Implement the steps to make a request to get the daily limit
//    }
//
//    @And("the server responds with the daily limit")
//    public void serverRespondsWithDailyLimit() {
//        // Implement the steps to assert the server's response with the daily limit
//    }

}
