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
import nl.inholland.Bank.API.model.dto.LoginDTO;
import nl.inholland.Bank.API.model.dto.UserRequestDTO;
import nl.inholland.Bank.API.model.dto.UserResponseDTO;
import org.junit.Assert;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class EmployeeSteps {
    private String employeeToken = "";
    private ResponseEntity<String> response;
    private UserRequestDTO registerInformation;


    @Given("the user has logged in as an employee")
    public void userLoggedInAsEmployee() {
        employeeToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJlbXBsb3llZUBpbmhvbGxhbmQuY29tIiwiYXV0aCI6WyJST0xFX0VNUExPWUVFIl0sImlhdCI6MTY4ODA2Nzg2NywiZXhwIjoxNjg4MTU0MjY3fQ.qrmCy9qzpdl48IzuVO2DKdTD8XG7gLXWmaZx4kLfwMOgogGniCfDdU_Uvw_hHNbdxIxjszqiNP37zMHkirTOAd4V4apV03SmBGkCxC5PT42waWrgO3jh6x8sUhz3HvFvhfzD07y4BIfDkTyAHg_Oo6MSXXBJD1Q-OlGst-U4K5p4q2rPzbRNjmkXhNIvSHVJVGTt2LMRwWCyESedX1D6GrxOFRefPSHZ2dqPUnLbGf3gMx3RCc55s5Ul-TAend3MRXl9Hr8XwXaYgSiH0KiDGuaZQrc0im8p6vN6FNLsWquGowopZ3W3LvCzhKXSZyCokkS9iGj86CgQEzLTABitoQ";
    }

    @When("the client requests to get all users")
    public void clientRequestsToGetAllUsers() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/users";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        this.response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }

    @Then("the server should respond with status code 200 for getting all users")
    public void serverRespondsToGetAllUsersWithStatusCode() {
        int actualStatusCode = this.response.getStatusCodeValue();
        Assert.assertEquals(200, actualStatusCode);
    }


    @And("the response body should contain a list of users")
    public void responseBodyContainsListOfUsers() {
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
    public void userProvidesValidUserDetails() {
        this.registerInformation = new UserRequestDTO("Test", "User", "test@user.com", "Test123!", "+4912345678", "12345678", "2000-10-10", "Mainstreet", 4, "1044CD", "Haarlem", "Netherlands");
    }

    @When("the client requests to register the user")
    public void clientRequestsToRegisterUser() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/users/register";

        HttpEntity<UserRequestDTO> requestEntity = new HttpEntity<>(this.registerInformation);

        this.response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
    }

    @Then("the server should respond with status code 200 for registering")
    public void responseBodyToRegisterContainsSuccessStatus() {
        int statusCode = response.getStatusCodeValue();
        Assert.assertEquals(200, statusCode);
    }
    @And("the response body should contain a success message for registering")
    public void responseBodyToRegisterContainsSuccessMessage(){
        String expectedSuccessMessage = "User created successfully";
        String responseBody = response.getBody();

        Assert.assertTrue("Response body does not contain the expected success message",
                responseBody.contains(expectedSuccessMessage));
    }
//
//    @When("the client requests to delete a user by ID")
//    public void clientRequestsToDeleteUserById() {
//        // Implement the steps to make a request to delete a user by ID
//    }
//
//    @Given("the employee provides a new daily limit")
//    public void employeeProvidesNewDailyLimit() {
//        // Implement the necessary steps for the employee to provide a new daily limit
//    }
//
//    @When("the client requests to update the user's daily limit")
//    public void clientRequestsToUpdateUserDailyLimit() {
//        // Implement the steps to make a request to update the user's daily limit
//    }
//
//    @And("the response body should contain the updated daily limit")
//    public void responseBodyContainsUpdatedDailyLimit() {
//        // Implement the steps to assert the presence of the updated daily limit in the response body
//    }
//
//    @Given("the employee provides a new transaction limit")
//    public void employeeProvidesNewTransactionLimit() {
//        // Implement the necessary steps for the employee to provide a new transaction limit
//    }
//
//    @When("the client requests to update the user's transaction limit")
//    public void clientRequestsToUpdateUserTransactionLimit() {
//        // Implement the steps to make a request to update the user's transaction limit
//    }
//
//    @And("the response body should contain the updated transaction limit")
//    public void responseBodyContainsUpdatedTransactionLimit() {
//        // Implement the steps to assert the presence of the updated transaction limit in the response body
//    }
//

}
