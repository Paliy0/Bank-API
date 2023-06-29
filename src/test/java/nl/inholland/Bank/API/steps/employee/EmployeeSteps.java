package nl.inholland.Bank.API.steps.employee;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.bs.A;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.inholland.Bank.API.controller.AuthController;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.*;
import org.junit.Assert;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmployeeSteps {
    private final long USER_ID = 1L;

    private String employeeToken = "";
    private ResponseEntity<String> response;
    private ResponseEntity<UserDLimitDTO>dailyLimitResponse;
    private ResponseEntity<UserTLimitDTO>transactionLimitResponse;

    private UserRequestDTO registerInformation;
    private int newDailyLimit;
    private int newTransactionLimit;


    @Given("the user has logged in as an employee")
    public void iUserLoggedInAsEmployee() {
        employeeToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJlbXBsb3llZUBpbmhvbGxhbmQuY29tIiwiYXV0aCI6WyJST0xFX0VNUExPWUVFIl0sImlhdCI6MTY4ODA2Nzg2NywiZXhwIjoxNjg4MTU0MjY3fQ.qrmCy9qzpdl48IzuVO2DKdTD8XG7gLXWmaZx4kLfwMOgogGniCfDdU_Uvw_hHNbdxIxjszqiNP37zMHkirTOAd4V4apV03SmBGkCxC5PT42waWrgO3jh6x8sUhz3HvFvhfzD07y4BIfDkTyAHg_Oo6MSXXBJD1Q-OlGst-U4K5p4q2rPzbRNjmkXhNIvSHVJVGTt2LMRwWCyESedX1D6GrxOFRefPSHZ2dqPUnLbGf3gMx3RCc55s5Ul-TAend3MRXl9Hr8XwXaYgSiH0KiDGuaZQrc0im8p6vN6FNLsWquGowopZ3W3LvCzhKXSZyCokkS9iGj86CgQEzLTABitoQ";
    }

    @When("the client requests to get all users")
    public void iClientRequestsToGetAllUsers() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/users";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        this.response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }

    @Then("the server should respond with status code 200 for getting all users")
    public void iServerRespondsToGetAllUsersWithStatusCode() {
        int actualStatusCode = this.response.getStatusCodeValue();
        Assert.assertEquals(200, actualStatusCode);
    }


    @And("the response body should contain a list of users")
    public void iResponseBodyContainsListOfUsers() {
        String responseBody = this.response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<User> users = objectMapper.readValue(responseBody, new TypeReference<List<User>>() {});

            assertNotNull(users);
            assertFalse(users.isEmpty());
        } catch (JsonProcessingException e) {
            fail("Error parsing response body: " + e.getMessage());
        }
    }


    @Given("the user provides valid user details")
    public void iUserProvidesValidUserDetails() {
        this.registerInformation = new UserRequestDTO("Test", "User", "test99@user.com", "Test123!", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");
    }

    @When("the client requests to register the user")
    public void iClientRequestsToRegisterUser() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/users/register";

        HttpEntity<UserRequestDTO> requestEntity = new HttpEntity<>(this.registerInformation);

        this.response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
    }

    @Then("the server should respond with status code 200 for registering")
    public void iResponseBodyToRegisterContainsSuccessStatus() {
        int statusCode = response.getStatusCodeValue();
        Assert.assertEquals(200, statusCode);
    }
    @And("the response body should contain a success message for registering")
    public void iResponseBodyToRegisterContainsSuccessMessage(){
        String expectedSuccessMessage = "User created successfully";
        String responseBody = response.getBody();

        Assert.assertTrue("Response body does not contain the expected success message",
                responseBody.contains(expectedSuccessMessage));
    }

    @When("the client requests to delete a user by ID")
    public void iClientRequestsToDeleteUserById() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/users/" + USER_ID;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);

        response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
    }

    @Then("the server should respond with status code 204 for deleting a user")
    public void iServerRespondsWithStatusCodeForDeletingUser() {
        int actualStatusCode = response.getStatusCodeValue();

        Assert.assertEquals("Unexpected status code", 204, actualStatusCode);
    }
    @Given("the employee provides a new daily limit")
    public void iEmployeeProvidesNewDailyLimit() {
        newDailyLimit = 1000;
    }
    @When("the client requests to update the user's daily limit")
    public void iClientRequestsToUpdateUserDailyLimit() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/users/" + USER_ID + "/dailyLimit?dailyLimit=" + newDailyLimit;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);

        dailyLimitResponse = restTemplate.exchange(url, HttpMethod.PUT, request, UserDLimitDTO.class);
    }

    @Then("the server should respond with status code 200 for update daily limit")
    public void iServerRespondsWithStatusCode() {
        int actualStatusCode = dailyLimitResponse.getStatusCodeValue();
        Assert.assertEquals("Unexpected status code", 200, actualStatusCode);
    }

    @And("the response body should contain the updated daily limit")
    public void iResponseBodyContainsUpdatedDailyLimit() {
        UserDLimitDTO updatedUserDLimit = dailyLimitResponse.getBody();

        String first = "1";
        String second = "" + updatedUserDLimit.userId();

        Assert.assertNotNull("Response body is null", updatedUserDLimit);
        Assert.assertEquals("Unexpected user ID in response", first, second);
        Assert.assertEquals("Unexpected updated daily limit", newDailyLimit, updatedUserDLimit.dailyLimit());
    }



    //transaction limit
    @Given("the employee provides a new transaction limit")
    public void iEmployeeProvidesNewTransactionLimit() {
        newTransactionLimit = 500;
    }
    @When("the client requests to update the user's transaction limit")
    public void iClientRequestsToUpdateUserTransactionLimit() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/users/" + USER_ID + "/transactionLimit?transactionLimit=" + newTransactionLimit;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);

        transactionLimitResponse = restTemplate.exchange(url, HttpMethod.PUT, request, UserTLimitDTO.class);
    }

    @Then("the server should respond with status code 200 for update transaction limit")
    public void iServerRespondsWithStatusCodeForUpdateTransactionLimit() {
        int actualStatusCode = transactionLimitResponse.getStatusCodeValue();
        Assert.assertEquals("Unexpected status code", 200, actualStatusCode);
    }

    @And("the response body should contain the updated transaction limit")
    public void iResponseBodyContainsUpdatedTransactionLimit() {
        UserTLimitDTO updatedUserTLimit = transactionLimitResponse.getBody();

        String first = "1";
        String second = "" + updatedUserTLimit.UserId();

        Assert.assertNotNull("Response body is null", updatedUserTLimit);
        Assert.assertEquals("Unexpected user ID in response", first, second);
        Assert.assertEquals("Unexpected updated transaction limit", newTransactionLimit, updatedUserTLimit.transactionLimit());
    }

}
