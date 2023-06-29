package nl.inholland.Bank.API;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/java/resources/features",
        glue = "nl.inholland.Bank.API.steps",
        plugin = "pretty",
        publish = true)
@ContextConfiguration
public class CucumberIT {
}