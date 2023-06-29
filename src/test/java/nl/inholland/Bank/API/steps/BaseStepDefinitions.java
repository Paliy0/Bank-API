package nl.inholland.Bank.API.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.inholland.Bank.API.model.dto.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(classes = CucumberContextConfig.class)
@Slf4j
public class BaseStepDefinitions {


    @Configuration
    public class ObjectMapperConfig {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Configuration
    public class TestRestTemplateConfig {
        @Bean
        public TestRestTemplate restTemplate() {
            return new TestRestTemplate();
        }
    }

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    protected ResponseEntity<String> response;
    protected HttpHeaders httpHeaders = new HttpHeaders();


    protected String getToken(LoginDTO loginDTO) throws com.fasterxml.jackson.core.JsonProcessingException {
        response = restTemplate.exchange("/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(objectMapper.writeValueAsString(loginDTO), httpHeaders), String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            if (responseBody != null) {
                LoginDTO tokenDTO = objectMapper.readValue(responseBody, LoginDTO.class);
                return tokenDTO.toString();
            } else {
                throw new IllegalStateException("Response body is null");
            }
        } else {
            throw new IllegalStateException("Failed to log in. Status code: " + response.getStatusCodeValue());
        }
    }

}
