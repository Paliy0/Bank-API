package nl.inholland.Bank.API.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.inholland.Bank.API.model.dto.AccountResponseDTO;
import nl.inholland.Bank.API.model.dto.LoginDTO;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.List;

public class AccountsStepDefinitions extends BaseStepDefinitions {

    @Given("I log in with the role {string}")
    public void iAmLoggedInAsEmployee(String role) throws com.fasterxml.jackson.core.JsonProcessingException {
        httpHeaders.clear();
        httpHeaders.add("Content-Type", "application/json");
        LoginDTO loginDTO;
        if (role.equalsIgnoreCase("role_employee")) {
            loginDTO = new LoginDTO("employee@inholland.com", "Test123!");
        } else if (role.equalsIgnoreCase("role_customer")) {
            loginDTO = new LoginDTO("customer@inholland.com", "Test123!");
        } else if (role.equalsIgnoreCase("role_user")) {
            loginDTO = new LoginDTO("user@inholland.com", "Test123!");
        } else {
            throw new IllegalArgumentException("Role is not valid");
        }
        httpHeaders.add("Authorization", "Bearer" + getToken(loginDTO));
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        Assertions.assertEquals(statusCode, response.getStatusCode().value());
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequest(String endpoint) {
        response = restTemplate.exchange(
                "/" + endpoint,
                HttpMethod.GET,
                new HttpEntity<>(
                        null,
                        httpHeaders),
                String.class);

    }

    @And("the response should be an array of objects")
    public void theResponseShouldBeAnArrayOfObjects() throws com.fasterxml.jackson.core.JsonProcessingException {
        List<AccountResponseDTO> accounts = objectMapper.readValue(response.getBody(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, AccountResponseDTO.class));
        Assertions.assertTrue(accounts.size() > 0);
    }
}