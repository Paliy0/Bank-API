package nl.inholland.Bank.API.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.inholland.Bank.API.model.dto.AccountResponseDTO;
import nl.inholland.Bank.API.model.dto.LoginDTO;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.List;

public class AccountsStepDefinitions extends BaseStepDefinitions {
    private String authenticationHeader;

    @Given("I log in with the role {string}")
    public void iAmLoggedInAsEmployee(String role) throws com.fasterxml.jackson.core.JsonProcessingException {
        httpHeaders.clear();
        httpHeaders.add("Content-Type", "application/json");

        LoginDTO loginDTO;
        if (role.equals("ROLE_EMPLOYEE")) {
            loginDTO = new LoginDTO("employee@inholland.com", "Test123!");
        } else if (role.equalsIgnoreCase("ROLE_CUSTOMER")) {
            loginDTO = new LoginDTO("customer@inholland.com", "Test123!");
        } else if (role.equalsIgnoreCase("ROLE_USER")) {
            loginDTO = new LoginDTO("user@inholland.com", "Test123!");
        } else {
            throw new IllegalArgumentException("Role is not valid");
        }
        httpHeaders.add("Authorization", "Bearer " + getToken(loginDTO));
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

        // Handle WWW-Authenticate header
        HttpHeaders responseHeaders = response.getHeaders();
        List<String> authenticateHeaders = responseHeaders.get("WWW-Authenticate");
        if (authenticateHeaders != null && !authenticateHeaders.isEmpty()) {
            String authenticateHeader = authenticateHeaders.get(0);
            // Extract the necessary information from the authenticateHeader and construct the authentication header for subsequent requests
            // For example, if using Basic Authentication, you might extract the realm and create the Authorization header as follows:
            //String realm = extractRealm(authenticateHeader);
            //authenticationHeader = "Basic " + encodeCredentials(username, password, realm);
        }

    }

    @And("the response should be an array of objects")
    public void theResponseShouldBeAnArrayOfObjects() throws com.fasterxml.jackson.core.JsonProcessingException {
        List<AccountResponseDTO> accounts = objectMapper.readValue(response.getBody(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, AccountResponseDTO.class));
        Assertions.assertTrue(accounts.size() > 0);
    }
}