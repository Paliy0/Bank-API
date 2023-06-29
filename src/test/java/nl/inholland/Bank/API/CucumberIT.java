package nl.inholland.Bank.API;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/java/resources/features",
        glue = "nl.inholland.Bank.API.steps",
        plugin = "pretty",
        publish = true)
public class CucumberIT {
}