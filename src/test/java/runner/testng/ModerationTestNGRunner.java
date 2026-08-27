package runner.testng;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/Features/moderationCenter.feature",
        glue = {"com.example.stepdefinitions", "com.example.hooks"},
        tags = "@moderation",
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber-report.html",
                "json:target/cucumber-reports/cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        }
)
public class ModerationTestNGRunner extends TestNGCucumberBase {
}
