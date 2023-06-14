package nl.inholland.Bank.API;

import nl.inholland.Bank.API.config.MyAppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MyAppProperties.class)
public class
BankApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankApiApplication.class, args);
    }

}