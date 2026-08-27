package runner.testng;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/Features/deletedAppUsers.feature",
        glue = {"com.example.stepdefinitions", "com.example.hooks"},
        tags = "not @ignore",
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber-report.html",
                "json:target/cucumber-reports/cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        }
)
public class DeletedAppUsersTestNGRunner extends TestNGCucumberBase {
}
