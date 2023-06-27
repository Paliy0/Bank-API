package nl.inholland.Bank.API.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bank-api")
public class MyAppProperties {
    private String defaultIban;

    public String getDefaultIban() {
        return defaultIban;
    }

    public void setDefaultIban(String defaultIban) {
        this.defaultIban = defaultIban;
    }
}
