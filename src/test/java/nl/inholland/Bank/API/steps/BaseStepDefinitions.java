package nl.inholland.Bank.API.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.inholland.Bank.API.model.dto.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@CucumberContextConfiguration
public class BaseStepDefinitions {

    @Autowired
    protected TestRestTemplate restTemplate;

    protected ObjectMapper objectMapper;

    protected ResponseEntity<String> response;
    protected HttpHeaders httpHeaders = new HttpHeaders();


    protected String getToken(LoginDTO loginDTO) throws com.fasterxml.jackson.core.JsonProcessingException {
        response = restTemplate
                .exchange("/auth/login",
                        HttpMethod.POST,
                        new HttpEntity<>(objectMapper.writeValueAsString(loginDTO), httpHeaders), String.class);
        LoginDTO tokenDTO = objectMapper.readValue(response.getBody(), LoginDTO.class);
        return tokenDTO.toString();
    }
}
